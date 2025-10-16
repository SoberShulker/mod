@Mod(modid = ExampleMod.MODID, version = ExampleMod.VERSION)
public class ExampleMod
{
    public static final String MODID = "examplemod";
    public static final String VERSION = "1.0";

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new JumpTest());
        System.out.println("Initialization event");
    }

    public static class JumpTest {
        private int tickCounter = 0;

        @SubscribeEvent
        public void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                tickCounter++;

                if (tickCounter >= 5) {
                    tickCounter = 0; // reset here

                    Minecraft mc = Minecraft.getMinecraft();
                    if (mc.thePlayer != null && mc.thePlayer.onGround) {
                        mc.thePlayer.motionY = 0.42F;
                        System.out.println("Jump applied!");
                    }
                }
            }
        }
    }
}
