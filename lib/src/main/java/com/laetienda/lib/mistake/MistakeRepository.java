package com.laetienda.lib.mistake;

import java.util.List;

public interface MistakeRepository {
	public List<Mistake> getMistakeByName(String pointer);
}
