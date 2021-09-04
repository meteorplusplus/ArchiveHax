package mathax.client.legacy.systems.enemies;

import mathax.client.legacy.systems.System;
import mathax.client.legacy.systems.Systems;
import mathax.client.legacy.systems.config.Config;
import mathax.client.legacy.utils.misc.NbtUtils;
import mathax.client.legacy.utils.player.ChatUtils;
import mathax.client.legacy.utils.render.MatHaxToast;
import mathax.client.legacy.utils.render.color.RainbowColors;
import mathax.client.legacy.utils.render.color.SettingColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static mathax.client.legacy.utils.Utils.mc;

public class Enemies extends System<Enemies> implements Iterable<Enemy> {
    private List<Enemy> enemies = new ArrayList<>();

    public final SettingColor color = new SettingColor(255, 0, 0);
    public boolean attack = false;

    public Enemies() {
        super("Enemies");
    }

    public static Enemies get() {
        return Systems.get(Enemies.class);
    }

    @Override
    public void init() {
        RainbowColors.add(color);
    }

    public boolean add(Enemy enemy) {
        if (enemy.name.isEmpty()) return false;
        if (enemy.name.equals(mc.getSession().getUsername())) {
            ChatUtils.error("Enemies", "You can't add yourself to enemies!");
            if (Config.get().chatCommandsToast) mc.getToastManager().add(new MatHaxToast(Items.REDSTONE_BLOCK, Formatting.DARK_RED + "Enemies", Formatting.RED + "You can't add yourself!"));
            return false;
        }

        if (!enemies.contains(enemy)) {
            enemies.add(enemy);
            save();

            return true;
        }

        return false;
    }

    public boolean remove(Enemy enemy) {
        if (enemies.remove(enemy)) {
            save();
            return true;
        }

        return false;
    }

    public Enemy get(String name) {
        for (Enemy Enemy : enemies) {
            if (Enemy.name.equals(name)) {
                return Enemy;
            }
        }

        return null;
    }

    public Enemy get(PlayerEntity player) {
        return get(player.getEntityName());
    }

    public boolean isEnemy(PlayerEntity player) {
        return get(player) != null;
    }

    public int count() {
        return enemies.size();
    }

    @Override
    public @NotNull Iterator<Enemy> iterator() {
        return enemies.iterator();
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();
        NbtList EnemiesTag = new NbtList();

        for (Enemy Enemy : enemies) EnemiesTag.add(Enemy.toTag());
        tag.put("Enemies", EnemiesTag);
        tag.put("color", color.toTag());
        tag.putBoolean("attack", attack);

        return tag;
    }

    @Override
    public Enemies fromTag(NbtCompound tag) {
        enemies = NbtUtils.listFromTag(tag.getList("Enemies", 10), tag1 -> new Enemy((NbtCompound) tag1));
        if (tag.contains("color")) color.fromTag(tag.getCompound("color"));
        attack = tag.contains("attack") && tag.getBoolean("attack");
        return this;
    }
}
