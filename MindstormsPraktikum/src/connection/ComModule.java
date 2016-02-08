package connection;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import connection.commands.Command;
import connection.commands.MoveElevatorDown;
import connection.commands.RequestElevator;
import connection.commands.RequestStatus;

public class ComModule {

	private final Command<Boolean> moveElevatorDown;
	private final Command<Boolean> requestElevator;
	private final Command<Boolean> requestStatus;

	public ComModule(URL baseUrl) {
		requestElevator = new RequestElevator(getCommandUrl(baseUrl, "go_up"));
		moveElevatorDown = new MoveElevatorDown(getCommandUrl(baseUrl, "go_down"));
		requestStatus = new RequestStatus(getCommandUrl(baseUrl, "status"));
	}

	private URL getCommandUrl(URL baseUrl, String command) {

		try {
			return new URL(baseUrl.toString() + "/" + command);
		} catch (MalformedURLException e) {
			throw new IllegalStateException("programming error, please revise hard coded URLs");
		}
	}

	public boolean requestStatus() throws IOException {
		return requestStatus.execute();
	}

	public boolean requestElevator() throws IOException {
		return requestElevator.execute();
	}

	public boolean moveElevatorDown() throws IOException {
		return moveElevatorDown.execute();
	}

}
