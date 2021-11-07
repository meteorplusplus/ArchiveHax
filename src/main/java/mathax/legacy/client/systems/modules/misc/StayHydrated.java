package mathax.legacy.client.systems.modules.misc;

import mathax.legacy.client.eventbus.EventHandler;
import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.settings.DoubleSetting;
import mathax.legacy.client.settings.EnumSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.systems.config.Config;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.utils.render.ToastSystem;
import mathax.legacy.client.utils.render.color.Color;
import net.minecraft.item.Items;

public class StayHydrated extends Module {
    private static final int BLUE = Color.fromRGBA(0, 128, 255, 255);

    private boolean menuCounting, notifyOnJoin, count;
    private int ticks = 0;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    //General

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Determines how to notify you when its time to drink.")
        .defaultValue(Mode.Both)
        .build()
    );

    private final Setting<Double> delay = sgGeneral.add(new DoubleSetting.Builder()
        .name("delay")
        .description("Delay between drinking notifications in minutes.")
        .defaultValue(120)
        .min(1)
        .sliderRange(5, 180)
        .build()
    );

    public StayHydrated() {
        super(Categories.Misc, Items.WATER_BUCKET, "stay-hydrated", "Notifies you when its time to drink. #StayHydrated");

        runInMainMenu = true;
    }

    @EventHandler
    public void onActivate() {
        ticks = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (count) ticks++;

        if (mc.world != null) count = true;

        if (notifyOnJoin && mc.world != null) {
            notifyOnJoin = false;
            ticks = 0;
            sendNotification();
            return;
        }

        if (mc.world == null) {
            menuCounting = true;
            if (ticks == (delay.get() * 20) * 60) {
                notifyOnJoin = true;
                count = false;
            }

            return;
        }

        if (ticks > (delay.get() * 20) * 60) {
            sendNotification();
            ticks = 0;
        }
    }

    private void sendNotification() {
        switch (mode.get()) {
            case Chat -> info("Its time to drink! #StayHydrated");
            case Toast -> mc.getToastManager().add(new ToastSystem(Items.WATER_BUCKET, BLUE, "Stay Hydrated", null, "Its time to drink!", Config.get().toastDuration));
            case Both -> {
                info("Its time to drink! #StayHydrated");
                mc.getToastManager().add(new ToastSystem(Items.WATER_BUCKET, BLUE, "Stay Hydrated", null, "Its time to drink!", Config.get().toastDuration));
            }
        }
    }

    public enum Mode {
        Chat,
        Toast,
        Both
    }
}
