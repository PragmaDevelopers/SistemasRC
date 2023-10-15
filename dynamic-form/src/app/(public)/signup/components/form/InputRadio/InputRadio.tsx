import { UseFormRegister,UseFormWatch } from "react-hook-form";
import InputsInterface from "../Interface/InputsInterface";

type ISimpleSelection = {
    register: UseFormRegister<InputsInterface>,
    className: string
}

export function CommonLawMarriage({register,className}:ISimpleSelection){
    return (
        <div className={className}>
            <span>Vive em União Estável: </span>
            <input required type="radio" value="true" id="input-true-common-law-marriage" {...register("common_law_marriage")} />
            <label htmlFor="input-common-law-marriage">Sim</label>
            <input required type="radio" value="false" id="input-false-common-law-marriage" {...register("common_law_marriage")} />
            <label htmlFor="input-common-law-marriage">Não</label>
        </div>
    )
}

type IAdvancedSelection = {
  register: UseFormRegister<InputsInterface>,
  watch: UseFormWatch<InputsInterface>,
  className: string
}

export function AddressComplement({register,watch,className}:IAdvancedSelection){
    return (
        <div className={className}>
            <span>Tipo de Complemento: </span>
              <input
                required
                type="radio"
                value="number"
                id="input-address-complement-number"
                {...register("address_complement_type")}
              />
              <label htmlFor="input-address-complement-number">Número</label>
              <input
                required
                type="radio"
                value="qd-lt"
                id="input-address-complement-qd-lt"
                {...register("address_complement_type")}
              />
              <label htmlFor="input-address-complement-qd-lt">Qd/Lt </label>
              {watch().address_complement_type === "number" ? (
                <input
                  placeholder="Adicione o número"
                  required
                  type="number"
                  {...register("address_complement_name")}
                />
              ) : watch().address_complement_type === "qd-lt" && (
                <input
                  placeholder="Adicione a quadra e lote"
                  required
                  type="text"
                  {...register("address_complement_name")}
                />
              )}
        </div>
    )
}