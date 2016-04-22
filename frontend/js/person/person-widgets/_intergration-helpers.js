let helpers = {
    _prepareSelectWithThemes($select, list){
        $select.children().remove();

        if (!list.length){
            $select.append(`<option value="" disabled checked>No available themes </option>`);
            return;
        }

        list.forEach((item)=>{
            $select.append(`<option value="${item.id}">${item.name}</option>`)
        });
    }
}

export default helpers;