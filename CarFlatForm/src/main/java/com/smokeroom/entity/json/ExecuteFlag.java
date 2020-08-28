package com.smokeroom.entity.json;
 

public   class ExecuteFlag{

	  
		private boolean offset;
	    
		private boolean parking;
	    
		private boolean danger;
	    
		private boolean limitspeed;
	    
		private boolean prohibit;
		
		private boolean reachingdestionation;
		
		
		
		public boolean getOffset() {
			return offset;
		}
		public void setOffset(boolean offset) {
			this.offset = offset;
		}
		public boolean getParking() {
			return parking;
		}
		public void setParking(boolean parking) {
			this.parking = parking;
		}
		public boolean getDanger() {
			return danger;
		}
		public void setDanger(boolean danger) {
			this.danger = danger;
		}
		public boolean getLimitspeed() {
			return limitspeed;
		}
		public void setLimitspeed(boolean limitspeed) {
			this.limitspeed = limitspeed;
		}
		public boolean getProhibit() {
			return prohibit;
		}
		public void setProhibit(boolean prohibit) {
			this.prohibit = prohibit;
		}
		
		public boolean getReachingdestionation() {
			return reachingdestionation;
		}
		public void setReachingdestionation(boolean reachingdestionation) {
			this.reachingdestionation = reachingdestionation;
		}
		
		@Override
		public String toString() {
			return "ExecuteFlag [offset=" + offset + ", parking=" + parking + ", danger=" + danger + ", limitspeed="
					+ limitspeed + ", prohibit=" + prohibit + ", reachingdestionation=" + reachingdestionation + "]";
		}
		
		 
		
	}