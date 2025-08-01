export default {
    checkFormRequiredValid(fieldList) {
        const errors = {};
        for(let field of fieldList) {
            if (!field.val) {
                if (!errors[field.name]) {
                    errors[field.name] = [];
                }
                errors[field.name].push(this.buildFormValidError(errors[field.name],"required",`${field.label ? field.label : field.name} 必须输入`,null,null));
            }
        }
        return errors;
    },
    buildFormValidError(error,type,message,checkFun,returnFun) {
        if (!error) {
            error = [];
        }
        if (checkFun) {
            if (checkFun()) {
                error.push({ type: type, message: message });
            }
        } else {
            error.push({ type: type, message: message });
        }
        if (returnFun) {
            returnFun(error);
        }
    }
}
