import { UseFormRegister } from "react-hook-form";
import InputsInterface from "../InputsInterface";

type SelectInterface = {
    register: UseFormRegister<InputsInterface>,
}

export function PowerOfAttorney({register}:SelectInterface){
    return (
        <div style={{marginBottom: 10}}>
            <label htmlFor="input-power-attorney">Procuração: </label>
            <select required defaultValue="default" id="input-power-attorney" {...register("power_of_attorney")}>
                <option disabled value="default">-- Escolha um tipo de procuração --</option>
                <option value="previdenciario">Previdenciário</option>
                <option value="trabalhista">Trabalhista</option>
                <option value="admnistrativo">Admnistrativo</option>
                <option value="civel">Cível</option>
            </select>
        </div>
    )
}

export function Ocuppation({register}:SelectInterface){
    return (
        <div style={{marginBottom: 10}}>
            {/* Futuramente criar um select com várias opções de cargo */}
            <label htmlFor="input-ocuppation">Profissão: </label>
            <input required type="text" id="input-ocuppation" {...register("ocuppation")} />
        </div>
    )
}