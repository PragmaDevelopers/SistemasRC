
public class Main {

	public static void main(String[] args) {
		IDandPasswords idPassword = new IDandPasswords();
		
		LoginPage loginPage =  new LoginPage(idPassword.GetLoginInfo());

	}

}
