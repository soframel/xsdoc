import org.scalatest.FunSuite
import java.io.File
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.sun.xml.xsom.parser.XSOMParser
import controllers.XMLSchemas
import play.api.Logger
import play.api.test.FakeApplication
import play.api.test.WithApplication
import controllers.Application

@RunWith(classOf[JUnitRunner])
class XMLSchemaTests extends FunSuite{

  test("generateId simple schema"){
    new WithApplication {
 	val is=this.getClass().getResourceAsStream("/schema1.xsd")
    val (set, map)=XMLSchemas.loadSchema(is)
    
    //test schema
    assert(2===set.getSchemaSize())
    val schema=set.getSchema(0)
    val schema2=set.getSchema(1)
    val testSchema=if(schema.getTargetNamespace()!=null && !schema.getTargetNamespace().equals("")) schema2 else schema    
    assert(1===testSchema.getElementDecls().size())
    
 	//check map
    val elementDecl=testSchema.getElementDecls().values().iterator().next()
    val elementId=Application.getIdForComponent(map, elementDecl)
    Logger.debug("id for elementDecl="+elementId)
    val comp=Application.getComponentForId(map, elementId)
    Logger.debug("comp for id = "+comp)
    assert(comp===elementDecl)
    }
  }
  
  test("generateId customer"){
    new WithApplication {
 	val is=this.getClass().getResourceAsStream("/customer.xsd")
    val (set, map)=XMLSchemas.loadSchema(is)
    
    //test schema
    assert(2===set.getSchemaSize())
    val schema=set.getSchema(0)
    val schema2=set.getSchema(1)
    val testSchema=if(schema.getTargetNamespace()!=null && schema.getTargetNamespace()!="") schema2 else schema
    
    assert(1===testSchema.getElementDecls().size())
    
 	//println("map contains "+map.size+" items")
    }
  }
}