package com.mojontwins.util;

public class TupleInt {
	public int x1, y1, x2, y2;
	public int n, s, w, e;
	
	public TupleInt(int x1, int y1, int x2, int y2) {
		this.x1 = this.n = x1;
		this.y1 = this.s = y1;
		this.x2 = this.w = x2;
		this.y2 = this.e = y2;
	}
}
