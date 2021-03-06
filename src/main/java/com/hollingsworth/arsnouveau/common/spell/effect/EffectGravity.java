package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EffectGravity extends AbstractEffect {

    public EffectGravity() {
        super(ModConfig.EffectGravityID, "Gravity");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(rayTraceResult instanceof BlockRayTraceResult){
            BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult) rayTraceResult;
            BlockPos pos = blockRayTraceResult.getPos();
            int aoeBuff = getBuffCount(augments, AugmentAOE.class);
            List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, pos, (BlockRayTraceResult)rayTraceResult,1 + aoeBuff, 1 + aoeBuff, 1, -1);
            for(BlockPos pos1 : posList) {
                if(world.getTileEntity(pos1) != null || !canBlockBeHarvested(augments, world, pos1))
                    continue;
                FallingBlockEntity blockEntity = new FallingBlockEntity(world,pos1.getX() +0.5, pos1.getY(), pos1.getZ() +0.5, world.getBlockState(pos1));
                world.addEntity(blockEntity);
            }
        }

        if(rayTraceResult instanceof EntityRayTraceResult){
            Entity entity = ((EntityRayTraceResult) rayTraceResult).getEntity();
            entity.setMotion(entity.getMotion().add(0, -1.0 - getAmplificationBonus(augments), 0));
            entity.velocityChanged = true;
        }
    }

    @Override
    public int getManaCost() {
        return 15;
    }

    @Override
    public Item getCraftingReagent() {
        return Items.ANVIL;
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    @Override
    protected String getBookDescription() {
        return "Causes blocks and entities to fall.";
    }
}
