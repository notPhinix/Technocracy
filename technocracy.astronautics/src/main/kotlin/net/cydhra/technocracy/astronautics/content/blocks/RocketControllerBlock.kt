package net.cydhra.technocracy.astronautics.content.blocks

import net.cydhra.technocracy.astronautics.content.entity.EntityRocket
import net.cydhra.technocracy.astronautics.content.tileentity.TileEntityRocketController
import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.client.gui.handler.TCGuiHandler
import net.cydhra.technocracy.foundation.data.world.groups.GroupManager
import net.cydhra.technocracy.foundation.model.blocks.api.AbstractRotatableTileEntityBlock
import net.cydhra.technocracy.foundation.model.blocks.util.IDynamicBlockPlaceBehavior
import net.cydhra.technocracy.foundation.network.componentsync.guiInfoPacketSubscribers
import net.cydhra.technocracy.foundation.util.structures.Template
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World


class RocketControllerBlock : AbstractRotatableTileEntityBlock("rocket_controller", material = Material.ROCK), IDynamicBlockPlaceBehavior {

    override fun placeBlockAt(place: Boolean, stack: ItemStack, player: EntityPlayer, world: World, pos: BlockPos, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, newState: IBlockState): Boolean {
        val tile = world.getTileEntity(pos) as? TileEntityRocketController ?: return place

        tile.ownerShip.setOwnerShip(GroupManager.getGroupFromUser(player.uniqueID))

        return place
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return TileEntityRocketController()
    }

    val launchpad = Template()
    val rocket_base = Template()
    val rocket_tip_a = Template()
    val rocket_tip_b = Template()
    val tank_module = Template()
    val dyson_cargo = Template()
    val satellite_cargo = Template()

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (!playerIn.isSneaking) {
            if (!worldIn.isRemote && hand == EnumHand.MAIN_HAND) {
                playerIn.openGui(TCFoundation, TCGuiHandler.machineGui, worldIn, pos.x, pos.y, pos.z)
                guiInfoPacketSubscribers[playerIn as EntityPlayerMP] = Pair(pos, worldIn.provider.dimension)
            }

            return true
        }

        if(true)
            return true


        if (worldIn.isRemote || hand != EnumHand.MAIN_HAND) {

            //TCParticleManager.addParticle(LaserBeam(worldIn, playerIn.posX, playerIn.posY, playerIn.posZ))


            return true//super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ)
        }

        val tile = worldIn.getTileEntity(pos) as TileEntityRocketController

        if (tile.ownerShip.currentOwner == null) {
            tile.ownerShip.setOwnerShip(GroupManager.getGroupFromUser(playerIn.uniqueID))
        }

        if (tile.ownerShip.currentOwner!!.getRights(playerIn.uniqueID) == GroupManager.PlayerGroup.GroupRights.NONE) {
            playerIn.sendMessage(TextComponentTranslation("rocket.controller.invalid.ownership"))
            return true
        }

        if (!launchpad.init) {
            launchpad.loadFromAssets("launchpad")
            rocket_base.loadFromAssets("rocket/rocket_base")
            rocket_tip_a.loadFromAssets("rocket/rocket_tip_a")
            rocket_tip_b.loadFromAssets("rocket/rocket_tip_b")
            tank_module.loadFromAssets("rocket/tank_module")
            dyson_cargo.loadFromAssets("rocket/storage_module")
            satellite_cargo.loadFromAssets("rocket/satellite_module")
        }

        if (tile.currentRocket != null) {
            playerIn.sendMessage(TextComponentTranslation("rocket.controller.invalid.already_linked"))
            return true
        }

        val matches = launchpad.matches(worldIn, pos, true)

        if (matches != null) {
            val base = rocket_base.matches(worldIn, pos, true)
            if (base != null) {

                val rotation = base.first

                val blocks = mutableListOf<BlockPos>()
                blocks.addAll(base.second)

                var offPos = pos.add(0, 4, 0)

                var tip = false
                var index = 0

                var totalStorageElements = 0
                var dysonCargo = 0
                var satelliteCargo = 0

                var tank = 0

                while (!tip && index < 10) {
                    index++

                    val match_tank = tank_module.matches(worldIn, offPos, true)
                    if (match_tank != null) {
                        blocks.addAll(match_tank.second)
                        if (totalStorageElements != 0) {
                            break
                        }

                        tank++
                        offPos = offPos.add(0, 3, 0)
                        continue
                    }

                    val match_storage = dyson_cargo.matches(worldIn, offPos, true, valid = { _, block, _ ->
                        if (block == rocketStorageBlock) {
                            dysonCargo++
                        }
                        block == rocketHullBlock || block == rocketStorageBlock
                    })

                    if (match_storage != null) {
                        blocks.addAll(match_storage.second)
                        totalStorageElements++
                        offPos = offPos.add(0, 3, 0)
                        continue
                    }

                    val match_satelite = satellite_cargo.matches(worldIn, offPos, true)

                    if (match_satelite != null) {
                        blocks.addAll(match_satelite.second)
                        totalStorageElements++
                        satelliteCargo++
                        offPos = offPos.add(0, 3, 0)
                        continue
                    }

                    //todo add rocket controller
                    var match_tip = rocket_tip_a.matches(worldIn, offPos, true, valid = { check_state, block, _ -> block == rocketHullBlock })

                    if (match_tip != null) {
                        blocks.addAll(match_tip.second)
                        tip = true
                        continue
                    }

                    match_tip = rocket_tip_b.matches(worldIn, offPos, true, valid = { check_state, block, _ -> block == rocketHullBlock })
                    if (match_tip != null) {
                        blocks.addAll(match_tip.second)
                        tip = true
                        continue
                    }
                }

                if (satelliteCargo != 0 && dysonCargo != 0) {
                    playerIn.sendMessage(TextComponentTranslation("rocket.controller.invalid.cantMix"))
                    return true
                }

                if (tip && tank * 2 >= totalStorageElements) {

                    //offset the position towards the rocket
                    val offset = pos.offset(rotation.rotate(EnumFacing.NORTH), -3)

                    val template = Template(offset, worldIn, blocks)
                    val ent = EntityRocket(worldIn, template, pos, tile.ownerShip.currentOwner!!)
                    //ent.motionY = 0.005
                    ent.setPosition(offset.x + 0.5, offset.y.toDouble(), offset.z + 0.5)
                    worldIn.spawnEntity(ent)

                    //16 buckets base rocket + 16 buckets for each tank module

                    ent.tank.fluid.capacity = (16 + tank * 16) * 1000

                    if (dysonCargo != 0) {
                        ent.dysonCargo = true
                        ent.cargoSlots = NonNullList.withSize(dysonCargo, ItemStack.EMPTY)
                    } else {
                        ent.cargoSlots = NonNullList.withSize(totalStorageElements, ItemStack.EMPTY)
                    }

                    ent.liftOff = true

                    tile.linkToCurrentRocket(ent)

                    for (e in blocks) {
                        worldIn.setBlockToAir(e)
                    }

                    playerIn.sendMessage(TextComponentString("rocket build: $totalStorageElements storage modules with $dysonCargo elements and $tank tank modules"))

                    return true
                }
            }
            playerIn.sendMessage(TextComponentTranslation("rocket.controller.invalid.rocket"))
        } else {
            playerIn.sendMessage(TextComponentTranslation("rocket.controller.invalid.launchpad"))
        }

        return true
    }

    override fun getDropItem(state: IBlockState, world: IBlockAccess, pos: BlockPos, te: TileEntity?): ItemStack {
        return ItemStack(this)
    }
}