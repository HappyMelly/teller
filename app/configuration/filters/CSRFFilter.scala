package configuration.filters

import javax.inject.{Inject, Provider}

import play.api.mvc.{RequestHeader, EssentialAction, EssentialFilter}
import play.filters.csrf.CSRF.{ConfigTokenProvider, DefaultErrorHandler, ErrorHandler, SignedTokenProvider, TokenProvider}
import play.filters.csrf.{CSRF, CSRFAction, CSRFConfig}

/**
  * A filter that provides CSRF protection.
  *
  * This CSRF filter doesn't apply to API post calls and login post call
  *
  * These must be by name parameters because the typical use case for instantiating the filter is in Global, which
  * happens before the application is started.  Since the default values for the parameters are loaded from config
  * and hence depend on a started application, they must be by name.
  *
  * @param config A csrf configuration object
  * @param tokenProvider A token provider to use.
  * @param errorHandler handling failed token error.
  */
class CSRFFilter(config: => CSRFConfig,
                 val tokenProvider: TokenProvider = SignedTokenProvider,
                 val errorHandler: ErrorHandler = CSRF.DefaultErrorHandler) extends EssentialFilter {

  @Inject
  def this(config: Provider[CSRFConfig], tokenProvider: TokenProvider, errorHandler: ErrorHandler) = {
    this(config.get, tokenProvider, errorHandler)
  }

  /**
    * Default constructor, useful from Java
    */
  def this() = this(CSRFConfig.global, new ConfigTokenProvider(CSRFConfig.global), DefaultErrorHandler)

  class CustomCSRFAction(next: EssentialAction,
                         config: CSRFConfig,
                         tokenProvider: TokenProvider,
                         errorHandler: => ErrorHandler) extends CSRFAction(next, config, tokenProvider, errorHandler) {

    override def apply(request: RequestHeader) = {
      val api = """/api/v""".r findPrefixOf request.path
      val userpass = """/authenticate/userpass""".r findPrefixOf request.path
      val webhooks = """/webhook""".r findPrefixOf request.path
      if (api.nonEmpty || userpass.nonEmpty || webhooks.nonEmpty)
        next(request)
      else
        super.apply(request)
    }
  }
  def apply(next: EssentialAction): EssentialAction = new CustomCSRFAction(next, config, tokenProvider, errorHandler)
}

object CSRFFilter {
  def apply(config: => CSRFConfig = CSRFConfig.global,
             tokenProvider: TokenProvider = new ConfigTokenProvider(CSRFConfig.global),
             errorHandler: ErrorHandler = DefaultErrorHandler): CSRFFilter = {
    new CSRFFilter(config, tokenProvider, errorHandler)
  }
}
