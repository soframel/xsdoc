package controllers

import play.api._
import play.api.Play.current
import play.api.mvc._
import play.twirl.api.Html
import java.io.File
import models.SchemasCollection
import scala.util.Random
import play.api.cache.Cache
import com.sun.xml.xsom._
import scala.collection.Map
import models.CompsWithIDs

object Application extends Controller {
  
  val userIdKey="userId"
  def schemaKey(id: String) = id+"_schemas"
  def mapKey(id: String) = id+"_map"

  def index = Action { request => {
    val id=getOrCreateUserId(request)
    Ok(views.html.upload()).withSession(userIdKey  -> id)    
  	}
  }
  
   def xsdUpload = Action(parse.multipartFormData) { request =>
  request.body.file("xsd").map { xsd =>    
    //copy file
    val filename = xsd.filename 
    val contentType = xsd.contentType
    Logger.info("Uploading file "+filename+" with content type "+contentType)
    val xsdFolder=new File("/tmp/xsd");
    if(!xsdFolder.exists)
      xsdFolder.mkdirs();
    val newFile=new File(xsdFolder, filename)
    xsd.ref.moveTo(newFile)
    
    //load file
    val (sset, map)=XMLSchemas.loadSchema(newFile)
    val coll=new SchemasCollection(sset)    
    
    val userId=getOrCreateUserId(request)
    Logger.debug("storing schemaCollection in cache with id "+schemaKey(userId))
    Cache.set(schemaKey(userId), coll)
    Cache.set(mapKey(userId), map)
    
    Ok(views.html.schemaSet(userId, coll.schemas)).withSession(userIdKey  -> userId)    
  }.getOrElse {
    Redirect(routes.Application.index).flashing(
      "error" -> "Missing file"
    )
  }
  }
   
   
  def getOrCreateUserId[T](request: Request[T]): String={
     request.session.get(userIdKey ) match{
      case None => {
    	  val id= IdGenerator.generateId
    	  Logger.debug("Generating user id:"+id)
    	  //request.session.+("userId", id)
    	  id
    	  }
      case Some(userId) => {
    	  Logger.debug("Reusing user id:"+userId)
    	  userId
        }
      }     
  }
  
  
   def getUserSchemasCollection[T](userId: String): SchemasCollection ={
   
     Cache.getOrElse[SchemasCollection](schemaKey(userId)){
       Logger.warn("schemaCollection not found in cache with id "+schemaKey(userId))
       null}
  }
  
 
  
  def xsComp(id: String) = Action {request => {   
     val userId=getOrCreateUserId(request)
    val coll=getUserSchemasCollection(userId)    
    val comp=getComponentForId(id, userId)
    
    comp match{
      case el: XSComponent => Ok(views.html.xsComp(userId, coll.schemas, el))    
      case o => InternalServerError("Oops, an error occured, element with id "+id+" is not an XSElementDecl: "+o)
    }
  }
  }
 
 
 def getIdForComponent(c: XSComponent, userId: String): String = { 
	  val map: CompsWithIDs=Cache.get(mapKey(userId)) match {
	    	case Some(m: CompsWithIDs) => m
	    	case Some(o) => {
	    	  Logger.warn("Cached map is not a CompsWithID: "+o.getClass+" ="+o)
	    	  new CompsWithIDs()
	    	} 
	    	case None => {
	    	  Logger.warn("Map not found")
	    	  new CompsWithIDs()
	    	}
	  }
	  getIdForComponent(map, c)
	}
    
    def getIdForComponent(map: CompsWithIDs, c: XSComponent): String = { 	  
	  map.findIdByComp(c)
	}
    
    def getComponentForId(id: String, userId: String): XSComponent = { 
	  val map: CompsWithIDs=Cache.get(mapKey(userId)) match {
	    	case Some(m: CompsWithIDs) => m
	    	case Some(o) => {
	    	  Logger.warn("Cached map is not a CompsWithID: "+o.getClass+" ="+o)
	    	  new CompsWithIDs()
	    	} 
	    	case None => {
	    	  Logger.warn("Map not found")
	    	  new CompsWithIDs()
	    	}
	  }
	  getComponentForId(map, id)	  
	}
    def getComponentForId(map: CompsWithIDs,id: String): XSComponent = { 
	  map.findCompById(id)
	}
    
}
  
  
