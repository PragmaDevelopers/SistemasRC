import java.util.HashMap;

public class IDandPasswords {
	HashMap<String, String> loginInfo = new HashMap<String, String>();
	
	IDandPasswords(){
		loginInfo.put( "abcdefghijklmnopqrstuvxwyz" , "abcdefghijklmnopqrstuvxwyz1234567890-=/*-+.,");
		loginInfo.put("ABCDEFGHIJKLMNOPQRSTUVXWYZ", "ABCDEFGHIJKLMNOPQRSTUVXWYZ1234567890-=+.,");
		loginInfo.put("!@#$%¨&*()_+-", "!@#$%¨&8()_+1234567890");
	}
	
	protected HashMap GetLoginInfo() {
		return loginInfo;
	}
}
