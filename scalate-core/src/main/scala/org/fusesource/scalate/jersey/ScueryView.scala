package org.fusesource.scalate.jersey

import org.fusesource.scalate.ResourceNotFoundException
import org.fusesource.scalate.scuery.Transformer
import org.fusesource.scalate.util.Logging

import xml.{XML, NodeSeq}
import javax.ws.rs.core.Context
import javax.servlet.ServletContext
import java.net.URL

/**
 * @version $Revision : 1.1 $
 */

trait ScueryView extends Logging {
  @Context
  private var _servletContext: ServletContext = _

  protected var templateDirectories = List("/WEB-INF", "")

  /**
   * Renders the given template URI using the given ScQuery transformer 
   */
  protected def render(template: String, transformer: Transformer): NodeSeq = {
    // lets load the template as XML...
    findResource(template) match {
      case Some(u) =>
        val xml = XML.load(u)
        // TODO report nice errors here if we can't parse it!!!
        transformer(xml)
      case _ => throw new ResourceNotFoundException(template)
    }
  }

  /**
   * Renders the index.html template using the given ScQuery transformer
   */
  protected def render(transformer: Transformer): NodeSeq = render("index.html", transformer)

  protected def findResource(path: String): Option[URL] = {
    val cname = getClass.getName
    println("Using class name: " + cname)
    val classDirectory = "/" + cname.replace('.', '/') + "."

    var answer: Option[URL] = None
    val paths = for (subDir <- List(classDirectory, ""); dir <- templateDirectories if answer.isEmpty) {
      val t = dir + subDir + path
      debug("Trying to find template: " + t)
      val u = servletContext.getResource(t)
      debug("Found: " + u)
      if (u != null) {
        answer = Some(u)
      }
    }
    answer
  }


  /**
   * Returns the servlet context injected by JAXRS
   */
  protected def servletContext: ServletContext = {
    if (_servletContext == null) {
      throw new IllegalArgumentException("servletContext not injected")
    }
    _servletContext
  }
}