package game.rps.project.RPSGame;
/**
* @author Yaakov Baker
* @version 1.0.5 1/25/21
*/
import java.util.Scanner;

public class RockPaperScissorsRunner 
{

	public static void main (String [] args)
	{
		Scanner scanner = new Scanner(System.in);
		System.out.print("Hi There, What's Your Name: ");
		String name = scanner.next();
		System.out.print("What do you want the game to be played to: ");
		int gameOutOf = scanner.nextInt();
		//take user input of which rps version they want to play
		System.out.println("If You Want To Play The Original Rock Paper Scissors Game Enter 1\n If You Want To Play Rock Paper Scissors Two Enter 2\n");
		int gameVersion = 1;
		while(gameVersion > 0 && gameVersion < 3)
		{
			gameVersion = scanner.nextInt();
			if( gameVersion == 1)
			{
				if ( gameOutOf < 1 )
				{
					System.out.println("\nYou can't play a game like that. I'll just set your game to 5.");
					gameOutOf = 5;
				}
				RockPaperScissors rPS = new RockPaperScissors(name, gameOutOf);
				rPS.rules();
				rPS.game();
			}else if( gameVersion == 2)
			{
				if ( gameOutOf < 1 )
				{
					System.out.println("\nYou can't play a game like that. I'll just set your game to 5.");
					gameOutOf = 5;
				}
				RockPaperScissors rPS2 = new RockPaperScissors2(name, gameOutOf);
				rPS2.rules();
				rPS2.game();
			}else
			{
				System.out.println("You Didn't Enter 1 or 2. Please Enter Either One Or Two.");
				gameVersion = 1;
				continue;
			}
		}
		
	}

}
