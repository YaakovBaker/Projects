package game.rps.project.RPSGame;
/**
* @author Yaakov Baker
* @version 1.0.5 3/4/21
*/

import java.util.Random;
import java.util.Scanner;

import game.rps.project.Game;

public class RockPaperScissors implements Game
{
	private String name;
	private boolean continueGame = true;
	private int delay = 200;
	private String[] prgmChoices = {"Rock", "Paper", "Scissors"};
	private int userScore = 0;
	private int prgmScore = 0;
	private int gameOutOf = 0;
	
	public RockPaperScissors(String name)
	{
		this(name, 5);
	}

	public RockPaperScissors(String name, int gameOutOf)
	{
		this.name = name;
		this.gameOutOf = gameOutOf;
	}
	
	@Override
	public void rules()
	{
		System.out.println("\nWelcome " + this.name + "!\n" + "This is the game Rock-Paper-Scissors...but against a computer program\n" + "You will be asked to input either Rock, Paper, or Scissors\n" +
		 "And the computer will randomly choose one.\n" + "In order to win you must win " + this.gameOutOf + " times...the same goes for the program\n" + "To end the game enter End instead of Rock, Paper, or Scissors\n" + 
		 "Well...hope you enjoy the game\n" + "P.S. There are some easter eggs");
	}

	@Override
	public void game()
	{
		System.out.println("\nLET THE GAME BEGIN!\n");
		Scanner gameInput = new Scanner(System.in);
		while (this.continueGame)
		{
			System.out.print("\nRock! ");
			try {
    			Thread.sleep(this.delay);
			} catch (InterruptedException ie) {
    			Thread.currentThread().interrupt();
			}
			System.out.print("Paper! ");
			try {
    			Thread.sleep(this.delay);
			} catch (InterruptedException ie) {
    			Thread.currentThread().interrupt();
			}
			System.out.print("Scissors! ");
			try {
    			Thread.sleep(this.delay);
			} catch (InterruptedException ie) {
    			Thread.currentThread().interrupt();
			}
			System.out.print("Shoot! ");
			String choice = gameInput.next();
			int rnd = new Random().nextInt(prgmChoices.length);
			String prgmChoice = prgmChoices[rnd];

			if ( ((choice.equalsIgnoreCase("Rock")) && (prgmChoice.equalsIgnoreCase("Scissors"))) ||
				 ((choice.equalsIgnoreCase("Paper")) && (prgmChoice.equalsIgnoreCase("Rock")))  ||
				 ((choice.equalsIgnoreCase("Scissors")) && (prgmChoice.equalsIgnoreCase("Paper"))) )
			{
				System.out.println("The program picked: " + prgmChoice);
				userRoundWin();
			}else if ( ((prgmChoice.equalsIgnoreCase("Rock")) && (choice.equalsIgnoreCase("Scissors"))) ||
				 	   ((prgmChoice.equalsIgnoreCase("Paper")) && (choice.equalsIgnoreCase("Rock")))  ||
				 	   ((prgmChoice.equalsIgnoreCase("Scissors")) && (choice.equalsIgnoreCase("Paper"))) )
			{
				System.out.println("The program picked: " + prgmChoice);
				prgmRoundWin();
			}else if ( choice.equalsIgnoreCase(prgmChoice) )
			{
				System.out.println("The program picked: " + prgmChoice);
				aTie();
			}else if ( choice.equalsIgnoreCase("end") )
			{
				this.continueGame = false;
			}else if ( choice.equalsIgnoreCase("shoot") )
			{
				userCheated();
			}else if ( !(choice.equalsIgnoreCase("Rock")) ||
					   !(choice.equalsIgnoreCase("Paper")) ||
					   !(choice.equalsIgnoreCase("Scissors")) )
			{
				System.out.println("Please Input Rock Paper or Scissors!");
				continue;
			}else
			{
				somethingIsWrong();
			}

			if ( (userScore == this.gameOutOf) || (prgmScore== this.gameOutOf) )
			{
				endOfGame();
			}
		}
	}

	private void userRoundWin()
	{
		this.userScore++;
		System.out.println(this.name + " won the round!");
		currentScore();
	}

	private void prgmRoundWin()
	{
		this.prgmScore++;
		System.out.println("Program won the round!");
		currentScore();
	}

	private void aTie()
	{
		System.out.println("This round was a tie!");
		currentScore();
	}

	private void userCheated()
	{
		System.out.println("\nUsing a Gun in Rock-Paper-Scissors! What a Cheater!");
		System.out.println("You lose a point for that, so try typing in something legal next time!\n");
		this.userScore--;
		currentScore();
	}

	private void somethingIsWrong()
	{
		System.out.println("Something went wrong. Forced prgram end");
		this.continueGame = false;
	}

	private void currentScore()
	{
		System.out.println("The Current Score Is:");
		System.out.println(this.name + " has a score of " + userScore);
		System.out.println("Program has a score of " + prgmScore);
	}

	private void endOfGame()
	{
		if (userScore == this.gameOutOf)
		{
			System.out.println("\n" + this.name + " is the Winner!");
			System.out.println(this.name + " has finished with a score of " + userScore + " and Progam has finished with a score of " + prgmScore);
			System.out.println("Thank You For Playing!");
			this.continueGame = false;
		}else if (prgmScore == this.gameOutOf)
		{
			System.out.println("\nWum...Wum...Wum...Wum...Wum...Program is the Winner!");
			System.out.println("Program has finished with a score of " + prgmScore + " and " + this.name + " has finished with a score of " + userScore);
			System.out.println("Thank You For Playing!");
			this.continueGame = false;
		}else
		{
			somethingIsWrong();
		}
	}
}