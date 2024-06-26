package edu.berkeley.cs186.database.concurrency;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility methods to track the relationships between different lock types.
 */
public enum LockType {
    S,   // shared
    X,   // exclusive
    IS,  // intention shared
    IX,  // intention exclusive
    SIX, // shared intention exclusive
    NL;  // no lock held

    /**
     * This method checks whether lock types A and B are compatible with
     * each other. If a transaction can hold lock type A on a resource
     * at the same time another transaction holds lock type B on the same
     * resource, the lock types are compatible.
     */
    public static boolean compatible(LockType a, LockType b) {
        if (a == null || b == null) {
            throw new NullPointerException("null lock type");
        }
        // TODO(proj4_part1): implement Done

        List<LockType> compatibleWithIS = new ArrayList<>(Arrays.asList(S, IS, IX, SIX, NL));
        List<LockType> compatibleWithIX = new ArrayList<>(Arrays.asList(IS, IX, NL));
        List<LockType> compatibleWithS = new ArrayList<>(Arrays.asList(S, IS, NL));
        List<LockType> compatibleWithSIX = new ArrayList<>(Arrays.asList(IS, NL));

        switch (a) {
            case NL: return true;
            case IS: return compatibleWithIS.contains(b);
            case IX: return compatibleWithIX.contains(b);
            case S : return compatibleWithS.contains(b);
            case SIX: return compatibleWithSIX.contains(b);
            case X : return (b == NL);
            default: throw new UnsupportedOperationException("bad lock type");
        }
    }

    /**
     * This method returns the lock on the parent resource
     * that should be requested for a lock of type A to be granted.
     */
    public static LockType parentLock(LockType a) {
        if (a == null) {
            throw new NullPointerException("null lock type");
        }
        switch (a) {
        case S: return IS;
        case X: return IX;
        case IS: return IS;
        case IX: return IX;
        case SIX: return IX;
        case NL: return NL;
        default: throw new UnsupportedOperationException("bad lock type");
        }
    }

    /**
     * This method returns if parentLockType has permissions to grant a childLockType
     * on a child.
     */
    public static boolean canBeParentLock(LockType parentLockType, LockType childLockType) {
        if (parentLockType == null || childLockType == null) {
            throw new NullPointerException("null lock type");
        }
        // TODO(proj4_part1): implement Done

        if(!parentLockType.isIntent()) return childLockType == NL;

        List<LockType> childOfIS = new ArrayList<>(Arrays.asList(S, IS, NL));
        List<LockType> childOfSIX = new ArrayList<>(Arrays.asList(X, IX, NL));

        switch (parentLockType) {
            case IS: return childOfIS.contains(childLockType);
            case IX: return true;
            case SIX: return childOfSIX.contains(childLockType);
            default: throw new UnsupportedOperationException("bad lock type");
        }
    }

    /**
     * This method returns whether a lock can be used for a situation
     * requiring another lock (e.g. an S lock can be substituted with
     * an X lock, because an X lock allows the transaction to do everything
     * the S lock allowed it to do).
     */
    public static boolean substitutable(LockType substitute, LockType required) {
        if (required == null || substitute == null) {
            throw new NullPointerException("null lock type");
        }
        // TODO(proj4_part1): implement Done

        switch (substitute) {
            case NL: return required == NL;
            case IS: return required == NL || required == IS;
            case IX: return required == NL || required == IS || required == IX;
            case S : return required == NL || required == IS || required == S;
            case SIX : return !(required == X);
            case X : return true;
            default: throw new UnsupportedOperationException("bad lock type");
        }
    }

    /**
     * @return True if this lock is IX, IS, or SIX. False otherwise.
     */
    public boolean isIntent() {
        return this == LockType.IX || this == LockType.IS || this == LockType.SIX;
    }

    @Override
    public String toString() {
        switch (this) {
        case S: return "S";
        case X: return "X";
        case IS: return "IS";
        case IX: return "IX";
        case SIX: return "SIX";
        case NL: return "NL";
        default: throw new UnsupportedOperationException("bad lock type");
        }
    }
}

