###
Happy Melly Teller
Copyright (C) 2013 - 2014, Happy Melly http://www.happymelly.com

This file is part of the Happy Melly Teller.

Happy Melly Teller is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Happy Melly Teller is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Happy Melly Teller.  If not, see <http://www.gnu.org/licenses/>.

If you have questions concerning this license or the applicable additional terms, you may contact
by email Sergey Kotlov, sergey.kotlov@happymelly.com or
in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
###

## Converts amounts between currencies and shows the exchange rates

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
      convertedElements = $(".#{this.id}.converted", scope)
      $.getJSON(url, (data) =>
        title = "Exchange rate #{baseCurrency}/#{targetCurrency} #{data.rate} (measured today)"
        resultText = "#{baseCurrency} #{conversionAmount} × #{data.rate} = #{data.result}"
        $(".#{this.id}.converted .amount", scope).text(resultText)
        convertedElements.attr('title', title).show()
      ).error(convertedElements.hide())
    else
      convertedElements.hide()

  $(counterElements).change(update)
  $(baseElements).change(->
    baseCurrency = baseCurrencyInput.val()
    baseAmount = baseAmountInput.val()
    $(counterElements).change()
  )
  $('.converted', scope).hide()

