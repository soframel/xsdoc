package controllers


import play.api.Logger
import java.io.File
import java.util.Collection
import com.sun.xml.xsom
import com.sun.xml.xsom.parser.XSOMParser
import com.sun.xml.xsom.XSAttContainer
import com.sun.xml.xsom.XSComplexType
import com.sun.xml.xsom.XSSchema
import com.sun.xml.xsom.XSSchemaSet
import com.sun.xml.xsom.util.DomAnnotationParserFactory
import com.sun.xml.xsom.XSComponent
import com.sun.xml.xsom.XSAnnotation
import com.sun.xml.xsom.XSElementDecl
import com.sun.xml.xsom.impl.AnnotationImpl
import org.xml.sax.ErrorHandler
import org.xml.sax.SAXParseException
import org.w3c.dom.Element
import scala.collection.JavaConversions._
import scala.util.Random
import scala.collection.Map
import com.sun.xml.xsom.XSSimpleType
import play.api.cache.Cache
import play.api.Play.current
import java.io.InputStream
import models.CompsWithIDs
import java.io.FileInputStream

object XMLSchemas {

   val defaultSchemaNS=List("http://www.w3.org/2001/XMLSchema")

	def loadSchema(file: File): (XSSchemaSet, CompsWithIDs) = {
		loadSchema(new FileInputStream(file))
	}
   
   def loadSchema(is: InputStream): (XSSchemaSet, CompsWithIDs) = {
		val parser = new XSOMParser()
		parser.setErrorHandler(new MyErrorHandler())
		val annFact=new DomAnnotationParserFactory()
		parser.setAnnotationParser(annFact)
		parser.parse(is)
		
		Logger.info("XMLSchema parsed")
		val sset=parser.getResult()
		
		val map=generateIds(sset)
		Logger.info("XSComponent IDs generated")

		(sset, map)
	}
   
    def getSubComponents(comp: XSComponent): Collection[XSComponent]={
     comp match{
       case t: XSComplexType => t.getContentType().asParticle().getTerm().asModelGroup().getChildren().toList
       case s: XSSchema => s.getComplexTypes().values()++s.getSimpleTypes().values()++s.getElementDecls().values()++s.getAttGroupDecls().values()++s.getAttributeDecls().values()++s.getModelGroupDecls().values()
       case c: XSAttContainer => c.getAttGroups()++c.getAttributeUses()    
       case e: XSElementDecl => { 
	         e.getType() match{
	         case t: XSComplexType => t.getContentType().asParticle().getTerm().asModelGroup().getChildren().toList
	         case o => List()
	       }        
       }   
       case o => List()
     }
   }

    def generateIds(sset: XSSchemaSet): CompsWithIDs={     
      val map=new CompsWithIDs()
      sset.getSchemas().map((xs: XSSchema) => generateIdsForComp(map)(xs))     
      map
   }
    
    def generateId(c: XSComponent) = c.hashCode()+""
    
    def generateIdsForComp(map: CompsWithIDs)(comp: XSComponent): Void={  
     val id=this.generateId(comp)  
     //Logger.debug("Generated id "+id+" for "+comp+" (class="+comp.getClass()+")")     
     map.addComp(id, comp)     
     getSubComponents(comp).map(generateIdsForComp(map))
     null
   }
    
}

class MyErrorHandler extends ErrorHandler{
  def error(e: SAXParseException) ={
    Logger.error("Error while parsing schema: "+e)
  }
         
 def fatalError(e: SAXParseException)={
   Logger.error("Fatal Error while parsing schema: "+e)
 }
       
 def warning(e: SAXParseException) ={
   Logger.warn("Warning while parsing schema: "+e)
 }
}