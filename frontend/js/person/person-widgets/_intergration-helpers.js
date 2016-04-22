let helpers = {
    _prepareSelectWithLists($select, list, $listName){
        $select.children().remove();

        if (!list.length){
            $select.append(`<option value="" disabled checked>No available lists </option>`);
            return;
        }

        list.forEach((item)=>{
            $select.append(`<option value="${item.id}">${item.name}</option>`)
        });
        $select.on('change', function(e) {
            $listName.val($select.find('option:selected').text());
        });
        $listName.val($select.find('option:selected').text());
    }
}

export default helpers;