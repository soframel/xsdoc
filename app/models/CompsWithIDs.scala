package models

import com.sun.xml.xsom.XSComponent

class CompsWithIDs {
	var mapById: Map[String,XSComponent] = Map[String,XSComponent]()
	var mapByComp: Map[XSComponent, String] = Map[XSComponent, String]()
  
	def addComp(id: String, comp: XSComponent)={
	  mapById = mapById.+(id->comp)
	  mapByComp = mapByComp.+(comp->id)
	}
	
	def findCompById(id: String): XSComponent={
	  mapById.get(id) match {
	    case None => null
	    case Some(c: XSComponent) => c
	    case c: XSComponent => c
	  }
	}
	
	def findIdByComp(comp: XSComponent): String={
	  mapByComp.get(comp) match{
	    case None => null
	    case Some(id: String) => id
	  }
	}
}