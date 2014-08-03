package controllers

import scala.util.Random
import java.lang.String

object IdGenerator {
	val random=new Random()
  
	def generateId(): String ={	
	  ""+random.nextString(20)
  }
}