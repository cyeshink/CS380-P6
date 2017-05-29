import java.io.*;
import java.net.*;
import java.util.*;

public class TicTacToeClient {

	public static void main(String[] args) throws Exception {
        try (Socket socket = new Socket("codebank.xyz", 38006)) {

			OutputStream os = socket.getOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(os);

			InputStream is = socket.getInputStream();
			ObjectInputStream in = new ObjectInputStream(is);

			Scanner kb = new Scanner(System.in);
			
			out.writeObject(new ConnectMessage("Christian"));
			out.writeObject(new CommandMessage(CommandMessage.Command.NEW_GAME));
			
			BoardMessage board = (BoardMessage)in.readObject();
			
			Object resp;
			ErrorMessage err = null;
			int row;
			int col;
			
			
			while(board.getStatus() == BoardMessage.Status.IN_PROGRESS && err == null) {
				
				printBoard(board);
				
				//Player Move
				System.out.print("Your move(rows and columns are from 0-2):\nEnter row: ");
				row = kb.nextInt();
				System.out.print("Enter column: ");
				col = kb.nextInt();
				out.writeObject(new MoveMessage((byte)row, (byte)col));
				
				
				//Server Response
				resp = in.readObject();
				if(resp instanceof BoardMessage) {
					board = (BoardMessage)resp;
				} else if(resp instanceof ErrorMessage) {
					err = (ErrorMessage)resp;
				}	
			}
			
			if(err != null) {
				System.out.println(err.getError());
			} else {
				System.out.print("Game Complete: ");
				if(BoardMessage.Status.PLAYER1_VICTORY == board.getStatus()) {
					System.out.println("You Win");
				} else if(BoardMessage.Status.PLAYER2_VICTORY == board.getStatus()) {
					System.out.println("Server Wins");
				} else {
					System.out.println("Cat\'s Game");
				}
			}
        }
	}
	
	
	public static void printBoard(BoardMessage board) {
		byte[][] ttt = board.getBoard();
		for(int x = 0; x < 3; x++) {
			for(int y = 0; y < 3; y++) {
				switch((int)ttt[x][y]) {
					case 0:
						System.out.print(" ");
						break;
					case 1:
						System.out.print("X");
						break;
					case 2:
						System.out.print("O");
				}
				if(y != 2)
					System.out.print("  |  ");
			}
            if(x==2){
                System.out.println();
                break;
            }
			System.out.println("\n-------------");
            
            
		}
	}
	
}