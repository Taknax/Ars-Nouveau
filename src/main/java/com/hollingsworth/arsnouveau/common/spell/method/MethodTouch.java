package com.hollingsworth.arsnouveau.common.spell.method;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;

import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class MethodTouch extends AbstractCastMethod {

    public MethodTouch() {
        super(ModConfig.MethodTouchID, "Touch");
    }

    @Override
    public int getManaCost() {
        return 5;
    }

    @Override
    public void onCast(ItemStack stack, LivingEntity caster, World world, List<AbstractAugment> augments, SpellContext context) { }

    @Override
    public void onCastOnBlock(ItemUseContext context, List<AbstractAugment> augments, SpellContext spellContext) {
        World world = context.getWorld();
        BlockRayTraceResult res = new BlockRayTraceResult(context.getHitVec(), context.getFace(), context.getPos(), false);
        resolver.onResolveEffect(world, context.getPlayer(), res);
        resolver.expendMana(context.getPlayer());
        Networking.sendToNearby(context.getWorld(), context.getPlayer(),
                new PacketANEffect(PacketANEffect.EffectType.BURST, res.getPos(), spellContext.colors));
    }

    @Override
    public void onCastOnBlock(BlockRayTraceResult res, LivingEntity caster, List<AbstractAugment> augments, SpellContext spellContext) {
        resolver.onResolveEffect(caster.getEntityWorld(),caster, res);
        resolver.expendMana(caster);
        Networking.sendToNearby(caster.world, caster, new PacketANEffect(PacketANEffect.EffectType.BURST, res.getPos(), spellContext.colors));
    }

    @Override
    public void onCastOnEntity(ItemStack stack, LivingEntity caster, LivingEntity target, Hand hand, List<AbstractAugment> augments, SpellContext spellContext) {
        resolver.onResolveEffect(caster.getEntityWorld(), caster, new EntityRayTraceResult(target));
        if(spellContext.getType() != SpellContext.CasterType.RUNE)
            resolver.expendMana(caster);
        Networking.sendToNearby(caster.world, caster, new PacketANEffect(PacketANEffect.EffectType.BURST, target.getPosition(), spellContext.colors));
    }

    @Override
    public boolean wouldCastSuccessfully(@Nullable ItemStack stack, LivingEntity playerEntity, World world, List<AbstractAugment> augments) {

        return false;
    }

    @Override
    public boolean wouldCastOnBlockSuccessfully(ItemUseContext context, List<AbstractAugment> augments) {
        World world = context.getWorld();
        BlockRayTraceResult res = new BlockRayTraceResult(context.getHitVec(), context.getFace(), context.getPos(), false);
        return resolver.wouldAllEffectsDoWork(res, world, context.getPlayer(), augments);
    }

    @Override
    public boolean wouldCastOnBlockSuccessfully(BlockRayTraceResult blockRayTraceResult, LivingEntity caster, List<AbstractAugment> augments) {
        return resolver.wouldAllEffectsDoWork(blockRayTraceResult, caster.getEntityWorld(), caster, augments);
    }

    @Override
    public boolean wouldCastOnEntitySuccessfully(@Nullable ItemStack stack, LivingEntity caster, LivingEntity target, Hand hand, List<AbstractAugment> augments) {
        return resolver.wouldAllEffectsDoWork(new EntityRayTraceResult(target), caster.getEntityWorld(), caster, augments);
    }

    @Override
    protected String getBookDescription() {
        return "Applies spells at the block or entity that is targeted.";
    }


    @Override
    public Item getCraftingReagent() {
        return Items.STONE_BUTTON;
    }
}
