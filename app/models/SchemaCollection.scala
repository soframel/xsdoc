package models

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
import controllers.XMLSchemas



class SchemasCollection(sset: XSSchemaSet){
    
  def schemas: List[Schema]={
    val schemas1=sset.getSchemas()
    def addSchemas(schemas1: Collection[XSSchema], acc: List[Schema] ): List[Schema]={
      if(schemas1.isEmpty())
        acc
       else{       
         if(XMLSchemas.defaultSchemaNS.contains(schemas1.head.getTargetNamespace()))
           addSchemas(schemas1.tail, acc:::List(new Schema(schemas1.head)))
         else
           addSchemas(schemas1.tail, new Schema(schemas1.head)::acc)
       }
    }
    addSchemas(schemas1, List())
  }
}

class Schema(val schema: XSSchema){
  def name: String={
    if(XMLSchemas.defaultSchemaNS.contains(schema.getTargetNamespace()))
      "XMLSchema Standard"
    else if(schema.getTargetNamespace()=="")
      "Default NS Schema"
    else
      schema.getTargetNamespace()
  }
}
