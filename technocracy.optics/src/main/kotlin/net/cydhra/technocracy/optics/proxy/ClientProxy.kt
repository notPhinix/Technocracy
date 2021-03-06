package net.cydhra.technocracy.optics.proxy


class ClientProxy : CommonProxy() {
    override fun init() {
        super.init()
        itemManager.registerItemColors()
        blockManager.registerBlockColors()
        tileEntityManager.onClientInitialize()
    }

    override fun preInit() {
        super.preInit()
        entityManager.registerRenderer()
    }
}