package it.polito.wa2.g19.server.common

import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import org.springframework.web.util.UriComponentsBuilder

class Util {

    companion object{
        fun getUri(handlerMapping: RequestMappingHandlerMapping, handlerName: String, vararg params: Any): String{
            val uri =  handlerMapping.handlerMethods.entries.find { it.value.method.name == handlerName}!!.key.pathPatternsCondition!!.patterns.first().patternString
            return UriComponentsBuilder.fromUriString(uri)
                .buildAndExpand(*params)
                .toUriString()
        }
    }
}