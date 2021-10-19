package edu.yu.cs.intro.doomGame;

import java.util.SortedSet;
import java.util.TreeMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.SortedMap;
/**
 * Plays through a given game scenario. i.e. tries to kill all the monsters in all the rooms and thus complete the game, using the given set of players
 */
public class CopyBot 
{
    private SortedSet<Room> rooms;
    private SortedSet<Player> players;
    private Set<Room> completedRooms;
    private SortedSet<Room> notCompletedRooms;

    /**
     * Create a new "GameBot", i.e. a program that automatically "plays the game"
     * @param rooms the set of rooms in this game
     * @param players the set of players the bot can use to try to complete all rooms
     */
    public CopyBot(SortedSet<Room> rooms, SortedSet<Player> players) 
    {
        this.rooms = rooms;
        this.players = players;
        this.completedRooms = new HashSet<>();
        this.notCompletedRooms = new TreeSet<>();
    }

    /**
     * Try to complete killing all monsters in all rooms using the given set of players.
     * It could take multiple passes through the set of rooms to complete the task of killing every monster in every room.
     * This method should call #passThroughRooms in some loop that tracks whether all the rooms have been completed OR we
     * have reached a point at which no progress can be made. If we are "stuck", i.e. we haven't completed all rooms but
     * calls to #passThroughRooms are no longer increasing the number of rooms that have been completed, return false to
     * indicate that we can't complete the game. As long as the number of completed rooms continues to rise, keep calling
     * #passThroughRooms.
     *
     * Throughout our attempt/logic to play the game, we rely on and take advantage of the fact that Room, Monster,
     * and Player all implement Comparable, and the sets we work with are all SortedSets
     *
     * @return true if all rooms were completed, false if not
     */
    public boolean play() 
    {
        return this.passThroughRooms().containsAll(this.rooms);
    }

    /**
     * Pass through the rooms, killing any monsters that can be killed, and thus attempt to complete the rooms
     * @return the set of rooms that were completed in this pass
     */
    protected Set<Room> passThroughRooms() 
    {
        for( Room room : this.getNotCompletedRooms() )
        {
            System.out.println("Reached Line 60 in  passThroughRooms and the current room name is: " + room.getName() );
            if( room.isCompleted() )
            {
                continue;
            }
            for( Monster monster : room.getMonsters() )
            {
                int failedToKill = 0;
                if( room.isCompleted() )
                {
                    break;
                }
                System.out.println("Reached Line 72 in  passThroughRooms and the current monster is: " + monster.getMonsterType() );
                if( monster.isDead() )
                {
                    continue;
                }
                for( Player player : this.players )
                {
                    System.out.println("Reached Line 79 in  passThroughRooms and the current player is: " + player.getName() );
                    if( canKill(player, monster, room) )
                    {
                        System.out.println("Reached Line 82 in passThroughRooms meansing we are able to kill the monster");
                        killMonster(player, room, monster);
                        System.out.println("reached Line 84 in passThroughRooms meaning we killed the monster");
                    }else 
                    {
                        failedToKill++;
                        if( failedToKill >= this.players.size() )
                        {
                            return this.getCompletedRooms();
                        }
                        System.out.println("reached Line 92 in passThroughRooms meaning we could not kill the monster so we will go to the next player");
                        continue;     
                    }
                    if( room.isCompleted() )
                    {
                        System.out.println("Reached Line 97 in passThroughRooms meaning the room completion evaluates to: " + room.isCompleted() + " and " + player.getName() + " was the one to complete it");
                        reapCompletionRewards(player, room);
                        this.completedRooms.add(room);
                        System.out.println("Reached Line 100 in passThroughRooms: The Number of Completed Rooms are " + this.getCompletedRooms().size());
                        break;
                    }
                    if( monster.isDead() )
                    {
                        System.out.println("reached Line 105 in passThroughRooms meaning we the monster is dead so it is time to kil the next one");
                        break;
                    }
                }
            }
        }
        //Return the set of completed rooms.
        return this.getCompletedRooms();
    }

    /**
     * give the player the weapons, ammunition, and health that come from completing the given room
     * @param player
     * @param room
     */
    protected void reapCompletionRewards(Player player, Room room) 
    {
        for( Weapon weapon : room.getWeaponsWonUponCompletion() )
        {
            player.addWeapon(weapon);
        }
        //need to iterate through the keyset() of the map and get the value for that weapon and put it in the parameter
        for( Weapon weapon : room.getAmmoWonUponCompletion().keySet() )
        {
            player.addAmmunition(weapon, room.getAmmoWonUponCompletion().get(weapon));
        }
        //health won
        player.changeHealth(room.getHealthWonUponCompletion());
    }

    /**
     * Have the given player kill the given monster in the given room.
     * Assume that #canKill was already called to confirm that player's ability to kill the monster
     * @param player
     * @param room
     * @param monsterToKill
     */
    protected void killMonster(Player player, Room room, Monster monsterToKill) 
    { 
        System.out.println("Reached Line 144 of killMonster meaning that the monsterToKill is " + monsterToKill.getMonsterType() + " and is protected by: " + monsterToKill.getProtectedBy());
        //Call getAllProtectorsInRoom to get a sorted set of all the monster's protectors in this room
        TreeSet<Monster> killOrder = new TreeSet<>();
        killOrder.addAll(getAllProtectorsInRoom(monsterToKill, room));
        System.out.println("Reached Line 148 of killMonster meaning the size of killOrder is: " + killOrder.size());
        if( (monsterToKill.getProtectedBy() != null) &&  (!killOrder.isEmpty()) ) 
        {
            //Player must kill the protectors before it can kill the monster, so kill all the protectors
            //first via a recursive call to killMonster on each one.
            System.out.println("Reached Line 153 of killMonster meaning the killOrder wasnt empty: " + killOrder.toString());
            Iterator<Monster> mnstrItr = killOrder.descendingIterator();
            Monster  protectMonster;
            while( mnstrItr.hasNext() )
            {
                protectMonster = mnstrItr.next();
                System.out.println("Reached Line 159 of killMonster meaning we are looping through the killOrder: The current monster to kill is: " + monsterToKill.getMonsterType() + " with memory address " + monsterToKill.toString());
                System.out.println("And the monster in the iterator aka its protector is: " + protectMonster.getMonsterType() + " with memory address " + protectMonster.toString());
                this.killMonster(player, room, protectMonster);
                
                if( protectMonster.isDead() )
                {
                    System.out.println("Reached Line 165 of killMonster meaning that the monsterToKill is not protected: " + monsterToKill.getMonsterType());
                    //Reduce the player's health by the amount given by room.getPlayerHealthLostPerEncounter().
                    player.changeHealth(0 - room.getPlayerHealthLostPerEncounter());
                    System.out.println("Reached Line 168 of killMonster meaning that the player's health changed: " + player.getHealth());
                    //Attack (and thus kill) the monster with the kind of weapon, and amount of ammunition, needed to kill it.
                    //figure out the weapon needed to kill the monster and the rounds and pass it as the weapon and rounds
                    //need to go through this players weapons and if it has a capable weapon then pass that
                    //also need to check if that weaon has the neccessary ammo needed to kill it
                    //
                    Weapon weaponUsed = monsterToKill.getMonsterType().weaponNeededToKill;
                    int ammoUsed = monsterToKill.getMonsterType().ammunitionCountNeededToKill;
                    if( player.hasWeapon(weaponUsed) )
                    {
                        System.out.println("Reached Line 178 of killMonster meaning that we have the necessary weapon to kill the monster");
                        monsterToKill.attack(weaponUsed, ammoUsed);
                        room.monsterKilled(monsterToKill);
                        System.out.println("Size of the live monsters: " + room.getLiveMonsters().size() + " the monster just killed: " + monsterToKill.toString() + " with name: " + monsterToKill.getMonsterType());
                    }else if( player.getWeapons().last().ordinal() > weaponUsed.ordinal() )
                    {
                        weaponUsed = player.getWeapons().last();
                        System.out.println("Reached Line 185 of killMonster meaning that we have the necessary weapon to kill the monster");
                        monsterToKill.attack(weaponUsed, ammoUsed);
                        room.monsterKilled(monsterToKill);
                        System.out.println("Size of the live monsters: " + room.getLiveMonsters().size() + " the monster just killed: " + monsterToKill.toString() + " with name: " + monsterToKill.getMonsterType());
                    }
                    player.changeAmmunitionRoundsForWeapon(weaponUsed, 0 - ammoUsed);
                    System.out.println("Reached Line 191 of killMonster meaning that the player's ammo changed: " + player.getAmmunitionRoundsForWeapon(weaponUsed) );
                    break;
                }
            }
        }else// it isnt protected then just attack it
        {
            System.out.println("Reached Line 197 of killMonster meaning that the monsterToKill is not protected: " + monsterToKill.getMonsterType());
            //Reduce the player's health by the amount given by room.getPlayerHealthLostPerEncounter().
            player.changeHealth(0 - room.getPlayerHealthLostPerEncounter());
            System.out.println("Reached Line 200 of killMonster meaning that the player's health changed: " + player.getHealth());
            //Attack (and thus kill) the monster with the kind of weapon, and amount of ammunition, needed to kill it.
            //figure out the weapon needed to kill the monster and the rounds and pass it as the weapon and rounds
            //need to go through this players weapons and if it has a capable weapon then pass that
            //also need to check if that weaon has the neccessary ammo needed to kill it
            //
            Weapon weaponUsed = monsterToKill.getMonsterType().weaponNeededToKill;
            int ammoUsed = monsterToKill.getMonsterType().ammunitionCountNeededToKill;
            if( player.hasWeapon(weaponUsed) )
            {
                System.out.println("Reached Line 210 of killMonster meaning that we have the necessary weapon to kill the monster");
                monsterToKill.attack(weaponUsed, ammoUsed);
                room.monsterKilled(monsterToKill);
                System.out.println("Size of the live monsters: " + room.getLiveMonsters().size() + " the monster just killed: " + monsterToKill.toString() + " with name: " + monsterToKill.getMonsterType());
            }
            player.changeAmmunitionRoundsForWeapon(weaponUsed, 0 - ammoUsed);
            System.out.println("Reached Line 216 of killMonster meaning that the player's ammo changed: " + player.getAmmunitionRoundsForWeapon(weaponUsed) );
        }
    }

    /**
     * @return a set of all the rooms that have been completed
     */
    public Set<Room> getCompletedRooms() 
    {
        // for( Room room : this.rooms )
        // {
        //     if( room.isCompleted() )
        //     {
        //         this.completedRooms.add(room);
        //     }
        // }
        return this.completedRooms;
    }

    /**
     * @return a set of all the rooms that have not been completed
     */
    protected SortedSet<Room> getNotCompletedRooms()
    {
        for( Room room : this.rooms )
        {
            if( !room.isCompleted() )
            {
                this.notCompletedRooms.add(room);
            }
        }
        return this.notCompletedRooms;
    }

    /**
     * @return an unmodifiable collection of all the rooms in the came
     * @see java.util.Collections#unmodifiableSortedSet(SortedSet)
     */
    public SortedSet<Room> getAllRooms() 
    {
        return java.util.Collections.unmodifiableSortedSet(this.rooms);
    }

    /**
     * @return a sorted set of all the live players in the game
     */
    protected SortedSet<Player> getLivePlayers() 
    {
        SortedSet<Player> livePlayers = new TreeSet<>();
        for( Player player : this.players )
        {
            if( !player.isDead() )
            {
                livePlayers.add(player);
            }
        }
        return livePlayers;
    }

    /**
     * @param weapon
     * @param ammunition
     * @return a sorted set of all the players that have the given wepoan with the given amount of ammunition for it
     */
    protected SortedSet<Player> getLivePlayersWithWeaponAndAmmunition(Weapon weapon, int ammunition) 
    {
        SortedSet<Player> livePlayersWithWeaponAndAmmunition = new TreeSet<>();
        for( Player player : this.players )
        {
            if( !player.isDead() && player.hasWeapon(weapon) && ammunition <= player.getAmmunitionRoundsForWeapon(weapon) )
            {
                livePlayersWithWeaponAndAmmunition.add(player);
            }
        }
        return livePlayersWithWeaponAndAmmunition;
    }

    /**
     * Get the set of all monsters that would need to be killed first before you could kill this one.
     * Remember that a protector may itself be protected by other monsters, so you will have to recursively check for protectors
     * @param monster
     * @param room
     * @return
     */
    protected static SortedSet<Monster> getAllProtectorsInRoom(Monster monster, Room room) 
    {
        return getAllProtectorsInRoom(new TreeSet<Monster>(), monster, room); //this is a hint about how to handle canKill as well
    }

    private static SortedSet<Monster> getAllProtectorsInRoom(SortedSet<Monster> protectors, Monster monster, Room room) 
    {
    //    if this monster has a protector in this room
    //     then add it to protectors
    //         if this protector has a protector call get all protectors for it
    //         then add what was returned to the protectors
    //         this then puts an order of what to kill so if the monster is a demon and its protecteed by baron and that is protected by spectre then the kill order will be spectre baron then demon
       
        for( Monster liveMonster : room.getLiveMonsters() )
        {
            if( liveMonster.getMonsterType() == monster.getProtectedBy() )
            {
                if( liveMonster.getMonsterType() != null )
                {
                    protectors.add(liveMonster);
                }
                protectors.addAll(getAllProtectorsInRoom(protectors, liveMonster, room));//new Monster(liveMonster.getMonsterType())
            }
        }
        return protectors;
    }

   /**
     * Can the given player kill the given monster in the given room?
     *
     * @param player
     * @param monster
     * @param room
     * @return
     * @throws IllegalArgumentException if the monster is not located in the room or is dead
     */
    protected static boolean canKill(Player player, Monster monster, Room room) 
    {
        int playerHealth = player.getHealth();
        if( !room.getMonsters().contains(monster) || monster.isDead() )
        {
            throw new IllegalArgumentException("the monster is not located in the room or is dead");
        }
        //Going into the room exposes the player to all the monsters in the room. If the player's health is
        //not > room.getPlayerHealthLostPerEncounter(), you can return immediately.
        if( playerHealth <= room.getPlayerHealthLostPerEncounter() )
        {
            return false;
        }
        //Call the private canKill method, to determine if this player can kill this monster.
        SortedMap<Weapon, Integer> roundsUsedPerWeapon = new TreeMap<>();
        roundsUsedPerWeapon.put(Weapon.FIST, 0);
        roundsUsedPerWeapon.put(Weapon.CHAINSAW, 0);
        roundsUsedPerWeapon.put(Weapon.PISTOL, 0);
        roundsUsedPerWeapon.put(Weapon.SHOTGUN, 0);
        Set<Monster> alreadyMarkedByCanKill = new HashSet<>();
        boolean canKill = canKill(player, monster, room, roundsUsedPerWeapon, alreadyMarkedByCanKill);
        //Before returning from this method, reset the player's health to what it was before you called the private canKill
        player.setHealth(playerHealth);
        return canKill;
    }

    /**
     *
     * @param player
     * @param monster
     * @param room
     * @param roundsUsedPerWeapon
     * @return
     */
    private static boolean canKill(Player player, Monster monster, Room room, SortedMap<Weapon, Integer> roundsUsedPerWeapon, Set<Monster> alreadyMarkedByCanKill) {
        //Remove all the monsters already marked / looked at by this series of recursive calls to canKill from the set of liveMonsters in the room before you check if the monster is alive and in the room. 
        //Be sure to NOT alter the actual set of live monsters in your Room object!
        SortedSet<Monster> liveMonstersForCanKill = new TreeSet<>();
        for( Monster m : room.getLiveMonsters() )
        {
            liveMonstersForCanKill.add(m);
            System.out.println("Reached Line 377 of canKill: Does the set contain that monster: " + m.toString() + " with name: " + m.getMonsterType() + " the evaluation: " + liveMonstersForCanKill.contains(m));
            System.out.println("Reached Line 378 of canKill: The size of the set currently: " + liveMonstersForCanKill.size());
        }
        System.out.println("liveMonnstersForCanKill contains all in the getlivemonsters: " + liveMonstersForCanKill.containsAll(room.getLiveMonsters()));
        liveMonstersForCanKill.removeAll(alreadyMarkedByCanKill);

        //Check if monster is in the room and alive.
        System.out.println(monster.toString() + " and this is its name " + monster.getMonsterType());
        if( liveMonstersForCanKill.contains(monster) )
        {
            System.out.println("Reached Line 387 of canKill: The monster is in the room and is not dead and contains evaluates to: " + liveMonstersForCanKill.contains(monster));
            //Check what weapon is needed to kill the monster, see if player has it. If not, return false.
            Weapon weaponForKill = monster.getMonsterType().weaponNeededToKill;
            if( (player.hasWeapon(weaponForKill)) || (player.getWeapons().last().ordinal() > weaponForKill.ordinal()) )
            {
                System.out.println("Reached Line 392 of canKill: The player has the necessary weapon to kill the monster: " + monster.getMonsterType());
                boolean canKillIt = true;
                //Check what protects the monster.
                if( monster.getProtectedBy() != null )
                {
                    System.out.println("Reached Line 397 of canKill: The monster: " + monster.getMonsterType() + " has a protector that is not null: " + monster.getProtectedBy());
                    //If the monster is protected,
                    TreeSet<Monster> mnstrProtectors = new TreeSet<>();
                    mnstrProtectors.addAll(getAllProtectorsInRoom(monster, room));
                    System.out.println("Reached Line 401 of canKill: The monster's protectors are: " + mnstrProtectors);
                    if( !mnstrProtectors.isEmpty() )
                    {
                        System.out.println("Reached Line 404 of canKill: The monster's protectors set is not empty: " + mnstrProtectors);
                        //the player can only kill this monster if it can kill its protectors as well.
                        //Make recursive calls to canKill to see if player can kill its protectors.
                        Iterator<Monster> mnstrItr = mnstrProtectors.descendingIterator();
                        while( mnstrItr.hasNext() )
                        {
                            Monster protectMonster = mnstrItr.next();
                            //Be sure to remove all members of alreadyMarkedByCanKill from the set of protectors before you recursively call canKill on the protectors.
                            if( alreadyMarkedByCanKill.contains(protectMonster) )
                            {
                                System.out.println("Reached Line 414 of canKill: The monster: " + protectMonster.toString() + " with type: " + protectMonster.getMonsterType() + " is already marked by canKill");
                                mnstrProtectors.remove(protectMonster);
                            }else
                            {
                                System.out.println("Reached Line 418 of canKill: The monster: " + protectMonster.toString() + " with type: " + protectMonster.getMonsterType() + " is not marked by canKill so recursion");
                                //Make recursive calls to canKill to see if player can kill its protectors.
                                canKillIt = canKill(player, protectMonster, room, roundsUsedPerWeapon, alreadyMarkedByCanKill);
                                if( !canKillIt )
                                {
                                    System.out.println("Reached Line 423 of canKill: the player: " + player.getName() + " could not kill the monster: " + monster.getMonsterType() + " and canKillIt evaluated to: " + canKillIt);
                                    return false;
                                }
                            }  
                        }
                        //Be sure to remove all members of alreadyMarkedByCanKill from the set of protectors before you recursively call canKill on the protectors.
                    } 
                }
                //If all the recursive calls to canKill on all the protectors returned true:
                if( canKillIt )
                {
                    System.out.println("Reached Line 434 of canKill: canKillIt evaluated to: " + canKillIt);
                    //Check what amount of ammunition is needed to kill the monster, and see if player has it after we subtract
                    //from his total ammunition the number stored in roundsUsedPerWeapon for the given weapon, if any.
                    int ammoNeededToKill = monster.getMonsterType().ammunitionCountNeededToKill;
                    int ammoPLayerHasFortheKill = player.getAmmunitionRoundsForWeapon(weaponForKill) - roundsUsedPerWeapon.get(weaponForKill);
                    //add how much ammunition will be used up to kill this monster to roundsUsedPerWeapon
                    if( ammoPLayerHasFortheKill >= ammoNeededToKill)
                    {
                        System.out.println("Reached Line 442 of canKill: the player: " + player.getName() + " has enough ammo to kill the monster: " + monster.getMonsterType());
                        //add how much ammunition will be used up to kill this monster to roundsUsedPerWeapon
                        roundsUsedPerWeapon.put(weaponForKill, roundsUsedPerWeapon.get(weaponForKill) + ammoPLayerHasFortheKill);
                    }else
                    {
                        System.out.println("Reached Line 447 of canKill: the player: " + player.getName() + " did not have enough ammo to kill the monster: " + monster.getMonsterType());
                        return false;
                    }
                    //Add up the playerHealthLostPerExposure of all the live monsters, and see if when that is subtracted from the player if his health is still > 0. If not, return false.
                    //If health is still > 0, subtract the above total from the player's health
                    int healthloss = 0;
                    int playerHealth = player.getHealth();
                    System.out.println("Reached Line 454 of canKill: the player: " + player.getName() + " has health: " + playerHealth);
                    for( Monster liveMonster : liveMonstersForCanKill )
                    {
                        healthloss += liveMonster.getMonsterType().playerHealthLostPerExposure;
                    }
                    int healthDifference = playerHealth - healthloss;
                    if( healthDifference > 0 )
                    {
                        //(Note that in the protected canKill method, you must reset the player's health to what it was before canKill was called before you return from that protected method)
                        //add this monster to alreadyMarkedByCanKill, and return true.
                        player.setHealth(healthDifference);
                        System.out.println("Reached Line 465 of canKill: the player: " + player.getName() + " has enough health " + player.getHealth() + " to kill the monster: " + monster.getMonsterType() + "\n");
                        alreadyMarkedByCanKill.add(monster);
                        return true;
                    }else
                    {
                        System.out.println("Reached Line 470 of canKill: the player: " + player.getName() + " did not have enough health " + player.getHealth() + " to kill the monster: " + monster.getMonsterType() + "\n");
                        return false;
                    }
                }else
                {
                    System.out.println("Reached Line 475 of canKill: the player: " + player.getName() + " could not kill the monster: " + monster.getMonsterType() + " and canKillIt evaluated to: " + canKillIt);
                    return false;
                }

            }else
            {
                System.out.println("Reached Line 481 of canKill: The player did not have the necessary weapon to kill the monster: " + monster.getMonsterType());
                return false;
            }
        }else
        {
            System.out.println("Reached Line 486 of canKill:  The monster is either not in the room or is dead and contains evaluates to: " + liveMonstersForCanKill.contains(monster));
            return false;
        }
    }
   
}
