$ = jQuery

$ ->
  scope = $('form')

  baseCurrencyInput = $('#source_currency', scope)
  baseAmountInput = $('#source_amount', scope)
  basePercentageInput = $('#source_percentage', scope)
  baseElements = [baseCurrencyInput.get(0), baseAmountInput.get(0), basePercentageInput.get(0)]

  fromAccountInput = $('#fromId', scope)
  toAccountInput = $('#toId', scope)
  counterElements = [fromAccountInput.get(0), toAccountInput.get(0)]

  baseCurrency = baseCurrencyInput.val()
  baseAmount = baseAmountInput.val()

  findCurrency = (accountSelect) -> $(accountSelect.options[accountSelect.selectedIndex]).data('currency')

  update = ->
    targetCurrency = findCurrency(this)
    if(targetCurrency)
      basePercentage = basePercentageInput.val()
      conversionAmount = Math.floor(baseAmount * basePercentage) / 100
      url = jsRoutes.controllers.ExchangeRates.convert("#{baseCurrency}#{conversionAmount}", targetCurrency).url
      $.getJSON(url, (data) =>
        title = "Exchange rate #{baseCurrency}/#{targetCurrency} #{data.rate} (measured today)"
        resultText = "#{baseCurrency} #{conversionAmount} × #{data.rate} = #{data.result}"
        $(".#{this.id}.converted .amount", scope).text(resultText)
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

