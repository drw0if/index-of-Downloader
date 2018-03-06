import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class code {
	static boolean debug = true;
	static final String userAgent = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2";
	
	public static void main(String... args) {
		
		try {
			searchInSite(new URL("http://index-of.es"));			
		
		} catch (IOException e) {
			if(debug)
				e.printStackTrace();
			System.err.println("It's impossible to reach the server!");
		}
		
	}
	
	public static void searchInSite(URL site) {
		
		try {
			
			String[] linkList = getLink(site);
			
			for(int i = 0; i < linkList.length; i++) {
				if	(linkList[i].contains(".png")) {
				
					if	(linkList[i].contains("/iconos/folder.png")) {
						String[] ahrefParsed = linkList[i].split("\"");
						System.out.println("DIR: " + ahrefParsed[1].replaceAll("%20", " "));
						searchInSite(new URL(site.toString() + ((site.toString().endsWith("/")) ? ahrefParsed[1] : ("/" + ahrefParsed[1]))));
					}
					else if	(linkList[i].contains("/iconos/") && !(linkList[i].contains("blank.png")) && !(linkList[i].contains("back.png"))) {
						String[] ahrefParsed = linkList[i].split("\"");
						download(new URL(site.toString() + ahrefParsed[1]));
					}	
				}
			}
			
		} catch(IOException e) {
			if(debug)
				e.printStackTrace();
		}
		
	}

		
	public static void download(URL toDownload) {
		
		//Creazione del buffer per la lettura
		byte buffer[] = new byte[1024];
		int buffered;
		
		try {
			//Apertura della connessione con il sito, spacciandosi per un browser
			URLConnection connection = toDownload.openConnection();
			connection.setRequestProperty("User-Agent", userAgent);
			
			//Ottenimento del nome del file
			String nameFile = toDownload.getPath();
			nameFile = nameFile.replaceAll("%20", " ");
			nameFile = nameFile.replaceFirst("/", "");
			
			//Creazione del file sul quale scrivere e relativo albero di cartelle
			File downloaded = new File(nameFile);
			if(downloaded.exists()) {
				System.out.println(downloaded.toString() + " esiste già, lo skippo");
				return;
			}
			downloaded.getParentFile().mkdirs();
			downloaded.createNewFile();
			
			//Apertura degli stream dal sito e verso il file
			InputStream webIn = connection.getInputStream();
			FileOutputStream fileOut = new FileOutputStream(downloaded);
			
			//Notifica a video di inizio download del file
			System.out.println("Downloading: " + downloaded.getPath());
			
			//Lettura dei file byte per byte
			while((buffered = webIn.read(buffer)) > 0)
				fileOut.write(buffer, 0, buffered);
			
			//Chiusura degli stream dal sito e verso il file
			fileOut.close();
			webIn.close();
		} catch (IOException e1) {
			//Se sono sotto debug ed avviene un errore lo stampo
			if(debug)
				e1.printStackTrace();
		}
	}
	
	public static String[] getLink(URL site) throws IOException {
		//Creazione dell'arraylist per i link
		ArrayList<String> linkRaw = new ArrayList<String>();
		
		//Apertura di una connessione con il sito, spacciandosi per un browser
		URLConnection connessione = site.openConnection();
		connessione.setRequestProperty("User-Agent", userAgent);
		//Apertura buffer di lettura
		BufferedReader lettore = new BufferedReader(new InputStreamReader(connessione.getInputStream()));
		
		//Se la stringa letta contiene un tag per il link la aggiungo all'arraylist
		String html = "";
		while((html = lettore.readLine()) != null)
			if(html.contains("a href"))
				linkRaw.add(html);
		
		//Chiusura del buffer di lettura
		lettore.close();
		
		//Conversione 
		String [] links = new String[linkRaw.size()];
		for(int i = 0; i < links.length; i++)
			links[i] = linkRaw.get(i);
		
		return links;
	}
	
}
