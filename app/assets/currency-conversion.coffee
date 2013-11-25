$ = jQuery

fx.base = "EUR"
fx.rates = {
  "USD" : 1.342099,
  "GBP" : 0.647710,
  "HKD" : 7.781919,
  "EUR" : 1
}

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
      if(fx.base != baseCurrency)
        console.error("Wrong base currency!")
        return
      rate = fx.rates[targetCurrency]
      if(!rate)
        console.warn("No rate for #{targetCurrency}")
        return

      result = Math.floor(fx(baseAmount).from(baseCurrency).to(targetCurrency) * 100) / 100
      title = "Exchange rate #{rate} (#{baseCurrency} to #{targetCurrency}, measured today"
      $(".#{this.id}.converted .amount", scope).text("#{targetCurrency} #{result}")
      $(".#{this.id}.converted", scope).attr('title', title).show()
    else
      $(".#{this.id}.converted", scope).hide()

  $(counterElements).change(update)
  $(baseElements).change(->
    baseCurrency = baseCurrencyInput.val()
    baseAmount = baseAmountInput.val()
    $(counterElements).change()
  )
  $('.converted', scope).hide()

