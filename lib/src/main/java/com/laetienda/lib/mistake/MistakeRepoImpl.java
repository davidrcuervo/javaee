package com.laetienda.lib.mistake;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MistakeRepoImpl implements MistakeRepository {
	final static private Logger log = LogManager.getLogger(MistakeRepoImpl.class);
	
	private Map<String, List<Mistake>> mistakes;
	
	public MistakeRepoImpl() {
		
	}
	
	public MistakeRepoImpl(List<Mistake> errores) {
		this.mistakes = new HashMap<String, List<Mistake>>();
		loadMistakes(errores);
	}

	private void loadMistakes(List<Mistake> errores) {
		
		if(errores != null) {
			for(Mistake m : errores) {
				
				String pointer = m.getSource().getPointer();
				log.debug("$pointer: {}", pointer);
				List<Mistake> t = mistakes.get(pointer);
				
				if(t == null) {
					t = new ArrayList<Mistake>();
					t.add(m);
					mistakes.put(pointer, t);
				}else {
					t.add(m);
				}
			}//for
		}//if	
	}

	@Override
	public List<Mistake> getMistakeByName(String pointer) {
		return mistakes.get(pointer);
	}
}
