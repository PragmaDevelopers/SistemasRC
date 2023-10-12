import { UseFormRegister } from "react-hook-form";
import InputsInterface from "../Interface/InputsInterface";

type SelectInterface = {
    register: UseFormRegister<InputsInterface>,
    marginBottom?: number | string
}

export function CommonLawMarriage({register,marginBottom}:SelectInterface){
    return (
        <div style={{marginBottom: marginBottom}}>
            <span>Vive em União Estável: </span>
            <input required type="radio" value="true" id="input-true-common-law-marriage" {...register("common_law_marriage")} />
            <label htmlFor="input-common-law-marriage">Sim</label>
            <input required type="radio" value="false" id="input-false-common-law-marriage" {...register("common_law_marriage")} />
            <label htmlFor="input-common-law-marriage">Não</label>
        </div>
    )
}