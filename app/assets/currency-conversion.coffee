$ = jQuery

$ ->
  scope = $('form')

  baseCurrencyInput = $('#source', scope)
  baseAmountInput = $('.source.amount', scope)
  baseElements = [baseCurrencyInput.get(0), baseAmountInput.get(0)]


  fromAccountInput = $('#fromId', scope)
  toAccountInput = $('#toId', scope)
  counterElements = [fromAccountInput.get(0), toAccountInput.get(0)]

  baseCurrency = baseCurrencyInput.val()
  baseAmount = baseAmountInput.val()

  findCurrency = (accountSelect) -> $(accountSelect.options[accountSelect.selectedIndex]).data('currency')

  update = ->
    targetCurrency = findCurrency(this)
    if(targetCurrency)
      url = jsRoutes.controllers.ExchangeRates.convert("#{baseCurrency}#{baseAmount}", targetCurrency).url
      $.getJSON(url, (data) =>
        title = "Exchange rate #{data.rate} (#{baseCurrency} to #{targetCurrency}, measured today"
        $(".#{this.id}.converted .amount", scope).text("#{data.result}")
        $(".#{this.id}.converted", scope).attr('title', title).show()
      )
    else
      $(".#{this.id}.converted", scope).hide()

  $(counterElements).change(update)
  $(baseElements).change(->
    baseCurrency = baseCurrencyInput.val()
    baseAmount = baseAmountInput.val()
    $(counterElements).change()
  )
  $('.converted', scope).hide()

