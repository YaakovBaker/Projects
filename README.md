# Projects
Repository containing notable projects from Yeshiva University courses and outside of school.

<p><a href = "https://github.com/YaakovBaker/CellSpreadSheet"><Strong>CellSpreadSheet</Strong></a>
  <br>•	Multi-file array-based spreadsheet written in Java. The <i>CellSpreadSheet</i> class implemented the <i>CellProvider</i> interface, and stores either a <i>FormulaCell</i> or <i>DoubleCell</i>, both implementing the <i>Cell</i> interface. The spreadsheet is constructed by passing in arguments into the <i>CellSpreadSheet</i> constructor specifying the row and column size of the spreadsheet. Afterwards the client can call a <i>setValue()</i> method to store data in the spreadsheet at the specified location. If the user wants a visualization of the spreadsheet then they can call <i>getSpreadSheetAsCSV(boolean)</i> by passing in true if they want it to show the formulas and false if they want the formulas to be calculated and then a spreadsheet in CSV format with those specifications is printed. A demo file can be found <a href = "https://github.com/YaakovBaker/CellSpreadSheet/blob/main/SpreadSheet/Assignment7Demo.java">here</a>.</P>

<p><a href = "https://github.com/YaakovBaker/Doom-Video-Game-Simulation"><Strong>Doom Video Game Simulation</Strong></a>
  <br>•	Coded a Video Game Simulation of Doom with Java as the final project for Intro to Computer Science. This consisted of two files containing enums for the Weapons, and MonsterTypes. The Player, Monster, and Room objects. And the class that ran the simulations: GameBot. A demo file can be found <a href = "https://github.com/YaakovBaker/Doom-Video-Game-Simulation/blob/main/edu/yu/cs/intro/doomGame/GameBotDemo.java">here</a> and a test file can be found <a href = "https://github.com/YaakovBaker/Doom-Video-Game-Simulation/blob/main/edu/yu/cs/intro/doomGame/Assignment9Tests.java">here</a>.</p> 
  
<p><a href = "https://github.com/YaakovBaker/Rock-Paper-Scissors"><Strong>Command Line Rock, Paper, Scissors</Strong></a>
  <br>•	Built a command line rock, paper, scissors game in Java. Printed to the terminal are the rules and instructions. The user must input how many wins they want to play to, so if they input 5 then the user or program, whichever reaches 5 wins first, wins the game. The program then randomly picks rock, paper, or scissors from an array of choices before the user does. The user then types their choice and the program compares the choices to see who wins the round and keeps track of the score and prints it to the terminal after each round. When a player reaches the goal and wins the program prints to the terminal the final score and messages depending on wether the user won or lose. There are also some command arguments like end then the program is terminated. There is also an easter egg.</p> 
  
<p><a href = "https://github.com/YaakovBaker/Document-Storage-System"><Strong>Document Storage System</Strong></a>
  <br>•	Built a Document Storage System with Java in Data Structures. It utilized various Data Structures we learned about in that course: Trie (searching), Stack (undo actions), Heap (last use time of a document), BTree (storage). The stored documents are instances of the <i>Document</i> and <i>DocumentImpl</i> classes and can be either text documents or documents of binary data. The <i>DocumentImpl</i> has multiple methods for returning specific and important information of the document like its wordCounts, the data stored, and its the last time it was used. The <i>DocumentPersistenceManager</i> class manipulates the state of these Documents by either serializing documents to the disk or deserializing them to bring them back to memory, and even deleting them when a deleteMethod is called. This is utilized by the Btree to manage its space in memory so that if the amount of documents exceed a set limit then instead of storing the document it will store a refrence to it on disk. Documents can be brought to memory when they are searched for by the Trie since their last use time will get updated and other documents can then be placed on disk to make space for this searched document.</p>
