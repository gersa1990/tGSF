
import de.irf.it.rmg.core.teikoku.kernel.events.JobCompletedEvent;
import de.irf.it.rmg.sim.kuiga.annotations.AcceptedEventType;


public class Test {

	/**
	 * @param args
	 */
	@AcceptedEventType(value=JobCompletedEvent.class)
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TestEnum.A.setMessage("A");
		TestEnum.B.setMessage("B");
		TestEnum.C.setMessage("C");
		
		for (TestEnum te : TestEnum.values()) {
			System.out.println(te.getMessage());
		}
	}
	
	public enum TestEnum {
		A,
		B,
		C;
		
		private String message;
		
		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
		
	}

}
