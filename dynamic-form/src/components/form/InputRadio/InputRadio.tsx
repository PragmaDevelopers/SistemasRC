import { UseFormRegister,UseFormSetValue,UseFormWatch } from "react-hook-form";
import { IFormSignUpInputs } from "@/Interface/IFormInputs";
import { useState,useEffect } from "react";
 
type ISimpleSelection = {
    register: UseFormRegister<IFormSignUpInputs>,
    className: string
}

type IAdvancedSelection = {
  register: UseFormRegister<IFormSignUpInputs>,
  watch: UseFormWatch<IFormSignUpInputs>,
  setValue: UseFormSetValue<IFormSignUpInputs>,
  className: string
}

export function CommonLawMarriage({register,className}:ISimpleSelection){
  return (
      <div className={className}>
          <span>Vive em União Estável: </span>
          <input type="radio" value="true" id="input-true-common-law-marriage" {...register("uniao_estavel",{required:true})} />
          <label className="me-2" htmlFor="input-common-law-marriage"> Sim</label>
          <input type="radio" value="false" id="input-false-common-law-marriage" {...register("uniao_estavel",{required:true})} />
          <label htmlFor="input-common-law-marriage"> Não</label>
      </div>
  )
}

export function AddressComplement({register,watch,setValue,className}:IAdvancedSelection){
  const [addressComplementValue,setAddressComplementValue] = useState("");
  useEffect(()=>{
    setValue("complemento_do_endereco","");
    setAddressComplementValue("");
  },[watch().tipo_de_complemento_do_endereco]);
  function formattedAddressComplement(addressComplement:string){
    if(watch().tipo_de_complemento_do_endereco === "number"){
      addressComplement = addressComplement.replace(/\D/g,""); //Substituí o que não é dígito por "", /g é [Global][1]
      setValue("complemento_do_endereco","nº "+addressComplement);
      setAddressComplementValue(addressComplement)
    }
    if(watch().tipo_de_complemento_do_endereco === "qd-lt"){
      addressComplement = addressComplement.replace(/^\D+$/g,"");
      addressComplement = addressComplement.replace(/^(\d+) (\d+)$/g,"Quadra $1 Lote $2"); //Substituí o que não é dígito por "", /g é [Global][1]
      setValue("complemento_do_endereco",addressComplement);
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
              {...register("tipo_de_complemento_do_endereco",{required:true})}
            />
            <label className="me-2" htmlFor="input-address-complement-number"> Número</label>
            <input
              type="radio"
              value="qd-lt"
              id="input-address-complement-qd-lt"
              {...register("tipo_de_complemento_do_endereco",{required:true})}
            />
            <label htmlFor="input-address-complement-qd-lt"> Qd/Lt </label>
          </div>
          <div>
            {watch().tipo_de_complemento_do_endereco === "number" ? (
              <input className="w-full" onChange={(e)=>formattedAddressComplement(e.target.value)} value={addressComplementValue}
                placeholder="Adicione o número"
                type="text"
              />
            ) : watch().tipo_de_complemento_do_endereco === "qd-lt" && (
              <input className="w-full" onChange={(e)=>formattedAddressComplement(e.target.value)} value={addressComplementValue}
                placeholder="Adicione o número da quadra e lote: nn nn"
                type="text"
              />
            )}
            <input type="hidden"  {...register("complemento_do_endereco",{required:true})} />
          </div>
      </div>
  )
}