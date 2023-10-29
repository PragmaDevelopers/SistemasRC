import { UseFormWatch } from "react-hook-form";
import { IFormSignUpInputs } from "@/Interface/IFormInputs";
import CepDataInterface from "@/Interface/ICepApi";

export async function tryGetAddressByCep(watch:UseFormWatch<IFormSignUpInputs>):Promise<CepDataInterface>{
    if(watch().cep?.replace("-","").length === 8){
        const data = await fetch(`https://viacep.com.br/ws/${watch().cep?.replace("-","")}/json/`).catch(error=>alert(error));
        if(data){
          const response = await data.json();
          return {
            uf: [response.uf],
            localidade: [response.localidade],
            bairro: [response.bairro],
            logradouro: [response.logradouro]
          }
        }
    }
    return {
      uf: [""],
      localidade: [""],
      bairro: [""],
      logradouro: [""]
    }
}

export async function getAddressManually(watch:UseFormWatch<IFormSignUpInputs>):Promise<CepDataInterface>{
    const data = await fetch(`https://viacep.com.br/ws/${watch().state_for_address}/${
      watch().city}/${watch().neighborhood}/json/`).catch(error=>alert(error));
      if(data){
        const response = await data.json();
        const newResponse:CepDataInterface = {
          uf: [],
          localidade: [],
          bairro: [],
          logradouro: []
        }
        response.forEach((address:{uf:"",localidade:"",bairro:"",logradouro:""})=>{
            if(newResponse.uf.indexOf(address.uf) == -1){
              newResponse.uf.push(address.uf)
            }
            if(newResponse.localidade.indexOf(address.localidade) == -1){
              newResponse.localidade.push(address.localidade)
            }
            if(newResponse.bairro.indexOf(address.bairro) == -1){
              newResponse.bairro.push(address.bairro)
            }
            if(newResponse.logradouro.indexOf(address.logradouro) == -1){
              newResponse.logradouro.push(address.logradouro)
            }
        });
        return newResponse;
      }
      return {
        uf: [""],
        localidade: [""],
        bairro: [""],
        logradouro: [""]
      }
}