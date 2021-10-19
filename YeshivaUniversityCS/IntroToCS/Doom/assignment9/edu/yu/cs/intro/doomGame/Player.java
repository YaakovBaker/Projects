package edu.yu.cs.intro.doomGame;

import java.util.Map;
import java.util.TreeSet;
import java.util.HashMap;
/**
 * Represents a player in the game.
 * A player whose health is <= 0 is dead.
 */
public class Player implements Comparable<Player> 
{
    private String name;
    private int health;
    private Map<Weapon, Integer> collectedWeaponsAndAmmo;
    private Map<Weapon, Integer> ammoBackpack;

    /**
     * @param name the player's name
     * @param health the player's starting health level
     */
    public Player(String name, int health)
    {
        this.name = name;
        this.health = health;
        this.collectedWeaponsAndAmmo = new HashMap<>();
        this.ammoBackpack = this.createAmmoBackpack();

        this.collectedWeaponsAndAmmo.put(Weapon.FIST, Integer.MAX_VALUE);
    }

    public String getName()
    {
        return this.name;
    }

    /**
     * does this player have the given weapon?
     * @param w
     * @return
     */
    public boolean hasWeapon(Weapon w)
    {
      return this.collectedWeaponsAndAmmo.keySet().contains(w);
    }

    /**
    *@return TreeSet of the weapons
    */
    protected TreeSet<Weapon> getWeapons()
    {
        return new TreeSet<>(this.collectedWeaponsAndAmmo.keySet());
    }
    /**
     * how much ammunition does this player have for the given weapon?
     * @param w
     * @return
     */
    public int getAmmunitionRoundsForWeapon(Weapon w)
    {
        if( this.hasWeapon(w) )
        {
            return this.collectedWeaponsAndAmmo.get(w);
        }else
        {
            return this.ammoBackpack.get(w);
        }
        
    }

    /**
     * Change the ammunition amount by a positive or negative amount
     * @param weapon weapon whose ammunition count is to be changed
     * @param change amount to change ammunition count for that weapon by
     * @return the new total amount of ammunition the player has for the weapon.
     */
    public int changeAmmunitionRoundsForWeapon(Weapon weapon, int change)
    {
        //change that weapons amunition by change and its a weapon we already have
        int ammo = getAmmunitionRoundsForWeapon(weapon) + change;// plus and if change is negative then it becomes minus
        if(ammo < 0)
        {
            ammo = 0;
        }
        if( this.hasWeapon(weapon) )
        {
            this.collectedWeaponsAndAmmo.put(weapon, ammo);
        }else
        {
            this.ammoBackpack.put(weapon, ammo);
        }
        return ammo;
        
    }

    /**
     * A player can have ammunition for a weapon even without having the weapon itself.
     * @param weapon weapon for which we are adding ammunition
     * @param rounds number of rounds of ammunition to add
     * @return the new total amount of ammunition the player has for the weapon
     * @throws IllegalArgumentException if rounds < 0 or weapon is null
     * @throws IllegalStateException if the player is dead
     */
    protected int addAmmunition(Weapon weapon, int rounds)
    {
        if( (rounds < 0) || (weapon == null) )
        {
            throw new IllegalArgumentException("You either have less than 0 rounds or don'r have the specified weapon");
        }else if( isDead() )
        {
            throw new IllegalStateException("The player is dead so you can't give him ammunition");
        }else if( !this.collectedWeaponsAndAmmo.keySet().contains(weapon) )//dont have the weapon so add the ammo to backpack
        {
            this.ammoBackpack.put(weapon, rounds);
            return this.ammoBackpack.get(weapon);//return backpack ammo
        }else//do have weapon so add the rounds
        {
            if( getAmmunitionRoundsForWeapon(weapon) > 0 )//if weapon has ammunition then add more rounds
            {
                this.collectedWeaponsAndAmmo.put(weapon, getAmmunitionRoundsForWeapon(weapon) + rounds);
                return getAmmunitionRoundsForWeapon(weapon);
            }else
            {
                this.collectedWeaponsAndAmmo.put(weapon, rounds);
                return getAmmunitionRoundsForWeapon(weapon);
            }
        }
    }

    /**
     * When a weapon is first added to a player, the player should automatically be given 5 rounds of ammunition.
     * If the player already has the weapon before this method is called, this method has no effect at all.
     * @param weapon
     * @return true if the weapon was added, false if the player already had it
     * @throws IllegalArgumentException if weapon is null
     * @throws IllegalStateException if the player is dead
     */
    protected boolean addWeapon(Weapon weapon)
    {
        if( weapon == null )
        {
            throw new IllegalArgumentException("Weapon is null");
        }else if( isDead() )
        {
            throw new IllegalStateException("Player is dead: can't give him a weapon");
        }else if( this.collectedWeaponsAndAmmo.keySet().contains(weapon) )
        {
            return false;
        }else
        {
            if( this.ammoBackpack.get(weapon) > 0 )//we have ammunition for this weapon then add the ammunition from backpack to the weapon
            {
                this.collectedWeaponsAndAmmo.put(weapon, this.ammoBackpack.get(weapon) + 5);
                this.ammoBackpack.put(weapon, 0);
                return true;
            }else//we don't have ammo in backpack for weapon
            {
                this.collectedWeaponsAndAmmo.put(weapon, 5);
                return true;
            }
        }
    }

    /**
     * Change the player's health level
     * @param amount a positive or negative number, to increase or decrease the player's health
     * @return the player's health level after the change
     * @throws IllegalStateException if the player is dead
     */
    public int changeHealth(int amount)
    {
        if( isDead() )
        {
            throw new IllegalStateException("Player is already dead: can't change his health at this point");
        }else
        {
            this.health += amount;
            return this.health;
        }
    }

    /**
     * set player's current health level to the given level
     * @param amount
     */
    protected void setHealth(int amount)
    {
        this.health = amount;
    }

    /**
     * get the player's current health level
     * @return
     */
    public int getHealth()
    {
        return this.health;
    }

    /**
     * is the player dead?
     * @return
     */
    public boolean isDead()
    {
        return this.health <= 0;
    }

    /**
     * @return the ammo backpack
     */
    protected Map<Weapon, Integer> createAmmoBackpack()
    {
        Map<Weapon, Integer> ammoPack = new HashMap<>();
        ammoPack.put(Weapon.FIST, 0);
        ammoPack.put(Weapon.CHAINSAW, 0);
        ammoPack.put(Weapon.PISTOL, 0);
        ammoPack.put(Weapon.SHOTGUN, 0);

        return ammoPack;
    }

    /**
     * Compare criteria, in order:
     * Does one have a greater weapon?
     * If they have the same greatest weapon, who has more ammunition for it?
     * If they are the same on weapon and ammunition, who has more health?
     * If they are the same on greatest weapon, ammunition for it, and health, they are equal.
     * Recall that all enums have a built-in implementation of Comparable, and they compare based on ordinal()
     *
     * @param other
     * @return
     */
    @Override
    public int compareTo(Player other) 
    {
        if(this.getWeapons().last().ordinal() < other.getWeapons().last().ordinal() )
        {
            return -1;
        }else if( this.getWeapons().last().ordinal() > other.getWeapons().last().ordinal() )
        {
            return 1;
        }else if( this.getWeapons().last().ordinal() == other.getWeapons().last().ordinal() )
        {
            if( this.getAmmunitionRoundsForWeapon(this.getWeapons().last()) < other.getAmmunitionRoundsForWeapon(other.getWeapons().last()) )
            {
                return -1;
            }else if( this.getAmmunitionRoundsForWeapon(this.getWeapons().last()) > other.getAmmunitionRoundsForWeapon(other.getWeapons().last()) )
            {
                return 1;
            }else if( this.getAmmunitionRoundsForWeapon(this.getWeapons().last()) == other.getAmmunitionRoundsForWeapon(other.getWeapons().last()) )
            {
                if( this.getHealth() < other.getHealth() )
                {
                    return -1;
                }else if( this.getHealth() > other.getHealth() )
                {
                    return 1;
                }else if( this.getHealth() == other.getHealth() )
                {
                    return 0;
                }
            }
        }
        return 1;
    }

    /**
     * Only equal if it is literally the same player
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) 
    {
        return  this == o;
    }

    /**
     * @return the hash code of the player's name
     */
    @Override
    public int hashCode() 
    {
        return getName().hashCode();
    }
}
