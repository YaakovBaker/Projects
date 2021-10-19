package edu.yu.cs.intro.doomGame;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map;

/**
 * A Room in the game, which contains both monsters as well as rewards for the player that completes the room,
 * which is defined as the player who kills the last living monster in the room
 */
public class Room implements Comparable<Room>
{
    private String name;
    private SortedSet<Monster> monsters;
    private Set<Weapon> weaponsWonUponCompletion;
    private Map<Weapon,Integer> ammoWonUponCompletion;
    private int healthWonUponCompletion;
    private SortedSet<Monster> liveMonsters;
    private SortedSet<Monster> deadMonsters;
    private int dangerLVL;

    /**
     *
     * @param monsters the monsters present in this room. This is a set of Monsters, NOT MonsterTypes - can have multiple monsters of a given type in a room
     * @param weaponsWonUponCompletion weapons a player gains when killing the last monster in this room
     * @param ammoWonUponCompletion ammunition a player gains when killing the last monster in this room
     * @param healthWonUponCompletion health a player gains when killing the last monster in this room
     * @param name the room's name
     */
    public Room(SortedSet<Monster> monsters, Set<Weapon> weaponsWonUponCompletion,Map<Weapon,Integer> ammoWonUponCompletion,int healthWonUponCompletion,String name)
    { 
        this.monsters = monsters;
        this.weaponsWonUponCompletion = weaponsWonUponCompletion;
        this.ammoWonUponCompletion = ammoWonUponCompletion;
        this.healthWonUponCompletion = healthWonUponCompletion;
        this.name = name;

        this.liveMonsters = new TreeSet<>();
        this.liveMonsters.addAll(monsters);

        this.deadMonsters = new TreeSet<>();
    }

    /**
     * Mark the given monster as being dead. Reduce the danger level of this room by
     * monster.getMonsterType().ordinal()+1
     * 
     * @param monster
     */
    protected void monsterKilled(Monster monster)
    {
        if( monster.isDead() )
        {
            //remove it from set of live monsters and into set of dead
            this.liveMonsters.remove(monster);
            this.deadMonsters.add(monster);
            
        }
        //reduce danger level
        this.dangerLVL -= monster.getMonsterType().ordinal() + 1;
    }

    /**
     * The danger level of the room is defined as the sum of the ordinal+1 value of all living monsters, i.e. adding up (m.getMonsterType().ordinal() + 1) of all the living monsters
     * @return the danger level of this room
     */
    public int getDangerLevel()
    {
        if( this.dangerLVL == 0)
        {
            for( Monster monster : this.liveMonsters )
            {
                this.dangerLVL += monster.getMonsterType().ordinal() + 1;
            }
        }
        return this.dangerLVL;
    }


    /**
     *
     * @return name of this room
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * compares based on danger level
     * @param other
     * @return
     */
    @Override
    public int compareTo(Room other) 
    {
        if( this.getDangerLevel() == other.getDangerLevel() )
        {
            return 0;
        }else if( this.getDangerLevel() < other.getDangerLevel() )
        {
            return -1;
        }else if( this.getDangerLevel() > other.getDangerLevel() )
        {
            return 1;
        }
        return 1;
    }


    /**
     * @return the set of weapons the player who completes the room is rewarded with. Make sure you don't allow the caller to modify the actual set!
     * @see java.util.Collections#unmodifiableSet(Set)
     */
    public Set<Weapon> getWeaponsWonUponCompletion()
    {
        return java.util.Collections.unmodifiableSet(this.weaponsWonUponCompletion);
    }

    /**
     * @return The set of ammunition the player who completes the room is rewarded with. Make sure you don't allow the caller to modify the actual set!
     * @see java.util.Collections#unmodifiableMap(Map)
     */
    public Map<Weapon,Integer> getAmmoWonUponCompletion()
    {
        return java.util.Collections.unmodifiableMap(this.ammoWonUponCompletion);
    }

    /**
     *
     * @return The amount of health this room
     */
    public int getHealthWonUponCompletion()
    {
        return this.healthWonUponCompletion;
    }

    /**
     * @return indicates if all the monsters in the room are dead
     */
    public boolean isCompleted()
    {
        return this.liveMonsters.isEmpty(); //meaning if there are no live monsters then room is completed so true 
    }

    /**
     * @return The SortedSet of all monsters in the room
     * @see java.util.Collections#unmodifiableSet(Set)
     */
    public SortedSet<Monster> getMonsters()
    {
        return java.util.Collections.unmodifiableSortedSet(this.monsters);
    }

    /**
     * @return the set of monsters in this room that are alive
     */
    public SortedSet<Monster> getLiveMonsters()
    {
        return this.liveMonsters;
    }

    /**
     * Every time a player enters a room, he loses health points based on the monster in the room.
     * The amount lost is the sum of the values of playerHealthLostPerExposure of all the monsters in the room
     * @return the amount of health lost
     * @see MonsterType#playerHealthLostPerExposure
     */
    public int getPlayerHealthLostPerEncounter()
    {
        int healthloss = 0;
        for( Monster monster : this.liveMonsters )
        {
            healthloss += monster.getMonsterType().playerHealthLostPerExposure;
        }
        return healthloss;
    }

    /**
     * @return the set of monsters in this room that are dead
     */
    public SortedSet<Monster> getDeadMonsters()
    {
        return this.deadMonsters;
    }
}