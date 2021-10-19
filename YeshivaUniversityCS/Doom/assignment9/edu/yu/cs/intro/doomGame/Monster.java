package edu.yu.cs.intro.doomGame;

/**
 * A specific monster
 */
public class Monster implements Comparable<Monster>
{
    private MonsterType type;
    private MonsterType protectedBy;
    private boolean dead;
    private Room room;
    private int leftoverHealth;
    /**
     * create a monster with no custom protectors; its protectors will be determined by its MonsterType
     * @param type the type of monster to create
     */
    protected Monster(MonsterType type)
    {
        this.type = type;
        this.protectedBy = type.getProtectedBy();
        this.dead = false;
        this.leftoverHealth = 0;
    }
    /**
     * create a monster with a custom protector, i.e. a different protector than the one specified in its MonsterType
     * @param type
     * @param customProtectedBy
     */
    public Monster(MonsterType type, MonsterType customProtectedBy)
    {
        this.type = type;
        this.protectedBy = customProtectedBy;
        this.dead = false;
        this.leftoverHealth = 0;
    }

    /**
     * set the room that the Monster is located in
     * @param room
     */
    protected void setRoom(Room room)
    {
        this.room = room;
    }

    protected Room getRoom()
    {
        return this.room;
    }

    public MonsterType getMonsterType()
    {
        return this.type;
    }

    /**
     * Attack this monster with the given weapon, firing the given number of rounds at it
     * @param weapon
     * @param rounds
     * @return indicates if the monster is dead after this attack
     * @throws IllegalArgumentException if the weapon is one that dones't hurt this monster, if the weapon is null, or if rounds < 1
     * @throws IllegalStateException if the monster is already dead
     */
    protected boolean attack(Weapon weapon, int rounds)
    {
        if( (weapon == null) || 
            (rounds < 1) || 
            ( this.type.weaponNeededToKill.ordinal() > weapon.ordinal() )
            )
        {
            throw new IllegalArgumentException("Can't attack becuase the weapon doesn't exist or you have less than one round(s) or this weapon cannot kill the monster");
        }else if( isDead() )
        {
            throw new IllegalStateException("Can't attack what is already dead");
        }else
        {
            if( this.leftoverHealth > 0 )
            {
                if( rounds >= this.leftoverHealth )//if the rounds is == to the ammount needed to kill then turn it dead and return isDead()
                {
                    this.setLifeStatusDead();
                    return isDead();
                }else //if the attack didn't kill it keep track of how many rounds left are needed to kill it and return isDead()
                {
                    //keep track of how many rounds left needed to kill
                    this.leftoverHealth = this.leftoverHealth - rounds; 
                    return isDead();
                }
            }else
            {
                if( rounds >= this.type.ammunitionCountNeededToKill )//if the rounds is == to the ammount needed to kill then turn it dead and return isDead()
                {
                    this.setLifeStatusDead();
                    return isDead();
                }else if( rounds < this.type.ammunitionCountNeededToKill )//if the attack didn't kill it keep track of how many rounds left are needed to kill it and return isDead()
                {
                    //keep track of how many rounds left needed to kill
                    this.leftoverHealth = this.type.ammunitionCountNeededToKill - rounds;
                    return isDead();
                }
            }
        }
        return isDead();
    }

    /**
     * @return is this monster dead?
     */
    public boolean isDead()
    {
        return this.dead;
    }

    protected void setLifeStatusDead()
    {
        this.dead = true;
    }

    /**
     * if this monster has its customProtectedBy set, return it. Otherwise, return the protectedBy of this monster's type
     * @return
     */
    public MonsterType getProtectedBy()
    {
        return this.protectedBy;
    }

    /**
     * Used to sort a set of monsters into the order in which they must be killed, assuming they are in the same room.
     * If the parameter refers to this monster, return 0
     * If this monster is protected by the other monster's type, return 1
     * If this monster's type protects the other monster, return -1
     * If this monster's ordinal is < the other's, return -1
     * If this monster's ordinal is > the other's, retuen 1
     * If(this.hashCode() < other.hashCode()), then return -1
     * Otherwise, return 1
     * @param other the other monster
     * @return see above
     */
    @Override
    public int compareTo(Monster other) {
        if(other == this){
            return 0;
        }else if(this.getProtectedBy() == other.getMonsterType()){
            return 1;
        }else if(other.getProtectedBy() == this.getMonsterType()){
            return -1;
        }else if(this.getMonsterType().ordinal() < other.getMonsterType().ordinal()){
            return -1;
        }else if(this.getMonsterType().ordinal() > other.getMonsterType().ordinal()){
            return 1;
        }else if(this.hashCode() < other.hashCode()){
            return -1;
        }
        return 1;
    }

}
