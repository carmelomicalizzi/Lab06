package it.polito.tdp.meteo;

import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.bean.Citta;
import it.polito.tdp.meteo.bean.SimpleCity;
import it.polito.tdp.meteo.db.MeteoDAO;

public class Model {
	
	MeteoDAO meteoDAO = new MeteoDAO();
	List<Citta> citta = new ArrayList<Citta>(meteoDAO.getCitta());
	List<SimpleCity> soluzione;
	private double bestScore;
	

	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;

	public Model() {
		

	}

	public String getUmiditaMedia(int mese) {
		
		String elenco = "";
		
		
		for(Citta c : meteoDAO.getCitta())
			
			elenco += c.getNome() +": " + meteoDAO.getAvgRilevamentiLocalitaMese(mese, c.getNome()) +"\n";

		return elenco;
	}
	
	public void reimpostaCitta(int mese) {
		
		for(Citta c : citta) {
			c.setRilevamenti(meteoDAO.getAllRilevamentiLocalitaMese(mese, c.getNome()));
			c.setCounter(0);
		}
			
	}

	public String trovaSequenza(int mese) {

		bestScore = Double.MAX_VALUE;
		
		this.reimpostaCitta(mese);
		
		soluzione = new ArrayList<SimpleCity>();		
		this.recursive(0, soluzione);
		
		return soluzione.toString() + this.punteggioSoluzione(soluzione);
	}
	
	public void recursive(int livello, List<SimpleCity> parziale) {
		
		if(livello >= NUMERO_GIORNI_TOTALI) {
			
		
		double score = 0.0;
		
		if(this.punteggioSoluzione(parziale) < bestScore) {
			bestScore = score;
			soluzione = new ArrayList<SimpleCity>(parziale);
		}
			
		return;
		}
		
		
		
		for(Citta c : citta) {
			 
				parziale.add(new SimpleCity(c.getNome(), c.getRilevamenti().get(livello).getUmidita()));
				c.increaseCounter();
				
				if(this.controllaParziale(parziale)) {
					recursive(livello+1, parziale);
				}
				
			parziale.remove(livello);	
			c.decreaseCounter();
			
		}
	}
	

	private Double punteggioSoluzione(List<SimpleCity> soluzioneCandidata) {

		
		SimpleCity previous = soluzioneCandidata.get(0);
		double score = 0.0;

		for (SimpleCity sc : soluzioneCandidata) {
			if (!previous.equals(sc)) {
				score += COST;
			}
			previous = sc;
			score += sc.getCosto();
		}

		return score;
	}

	private boolean controllaParziale(List<SimpleCity> parziale) {
		
		for(Citta c : citta) {
			if(c.getCounter() > NUMERO_GIORNI_CITTA_MAX)
				return false;
		}
		
		SimpleCity previous = parziale.get(0);
		int counter = 0;

		for (SimpleCity sc : parziale) {
			if (!previous.equals(sc)) {
				if (counter < NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN) {
					return false;
				}
				counter = 1;
				previous = sc;
			} else {
				counter++;
			}
		}

		return true;
		 
	}
	
}
