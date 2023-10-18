import { UseFormRegister,UseFormSetValue,UseFormWatch } from "react-hook-form";
import InputsInterface from "../Interface/InputsInterface";
import { useState,useEffect } from "react";
 
type ISimpleSelection = {
    register: UseFormRegister<InputsInterface>,
    className: string
}

type IAdvancedSelection = {
  register: UseFormRegister<InputsInterface>,
  watch: UseFormWatch<InputsInterface>,
  setValue: UseFormSetValue<InputsInterface>,
  className: string
}

export function CommonLawMarriage({register,className}:ISimpleSelection){
  return (
      <div className={className}>
          <span>Vive em União Estável: </span>
          <input type="radio" value="true" id="input-true-common-law-marriage" {...register("common_law_marriage",{required:true})} />
          <label className="me-2" htmlFor="input-common-law-marriage"> Sim</label>
          <input type="radio" value="false" id="input-false-common-law-marriage" {...register("common_law_marriage",{required:true})} />
          <label htmlFor="input-common-law-marriage"> Não</label>
      </div>
  )
}

export function AddressComplement({register,watch,setValue,className}:IAdvancedSelection){
  const [addressComplementValue,setAddressComplementValue] = useState("");
  useEffect(()=>{
    setValue("address_complement_name","");
    setAddressComplementValue("");
  },[watch().address_complement_type]);
  function formattedAddressComplement(addressComplement:string){
    if(watch().address_complement_type === "number"){
      addressComplement = addressComplement.replace(/\D/g,""); //Substituí o que não é dígito por "", /g é [Global][1]
      setValue("address_complement_name","Number:"+addressComplement);
      setAddressComplementValue(addressComplement)
    }
    if(watch().address_complement_type === "qd-lt"){
      addressComplement = addressComplement.replace(/^\D+$/g,"");
      addressComplement = addressComplement.replace(/^(\d+) (\d+)$/g,"Quadra $1 Lote $2"); //Substituí o que não é dígito por "", /g é [Global][1]
      setValue("address_complement_name","Qd/Lt:"+addressComplement);
      setAddressComplementValue(addressComplement)
    }
  }
  return (
      <div className={className}>
          <div>
            <span>Tipo de Complemento: </span>
            <input
              type="radio"
              value="number"
              id="input-address-complement-number"
              {...register("address_complement_type",{required:true})}
            />
            <label className="me-2" htmlFor="input-address-complement-number"> Número</label>
            <input
              type="radio"
              value="qd-lt"
              id="input-address-complement-qd-lt"
              {...register("address_complement_type",{required:true})}
            />
            <label htmlFor="input-address-complement-qd-lt"> Qd/Lt </label>
          </div>
          <div>
            {watch().address_complement_type === "number" ? (
              <input className="w-full" onChange={(e)=>formattedAddressComplement(e.target.value)} value={addressComplementValue}
                placeholder="Adicione o número"
                type="text"
              />
            ) : watch().address_complement_type === "qd-lt" && (
              <input className="w-full" onChange={(e)=>formattedAddressComplement(e.target.value)} value={addressComplementValue}
                placeholder="Adicione o número da quadra e lote: nn nn"
                type="text"
              />
            )}
            <input type="hidden"  {...register("address_complement_name",{required:true})} />
          </div>
      </div>
  )
}