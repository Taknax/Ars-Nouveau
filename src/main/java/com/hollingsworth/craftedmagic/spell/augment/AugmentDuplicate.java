package com.hollingsworth.craftedmagic.spell.augment;

public class AugmentDuplicate extends AugmentType{
    public AugmentDuplicate(String tag, String description) {
        super(tag, description);
    }

    @Override
    public int getManaCost() {
        return 0;
    }
}