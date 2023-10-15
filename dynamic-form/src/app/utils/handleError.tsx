import { UseFormWatch } from "react-hook-form";
import InputsInterface from "../(public)/signup/components/form/Interface/InputsInterface";
import CepDataInterface from "../(public)/signup/components/form/Interface/CepData";

export async function tryGetAddressByCep(watch:UseFormWatch<InputsInterface>):Promise<CepDataInterface>{
    if(watch().cep?.length === 8){
        const data = await fetch(`https://viacep.com.br/ws/${watch().cep}/json/`);
        const response = await data.json().catch(error=>alert(error));
        return {
          uf: [response.uf],
          localidade: [response.localidade],
          bairro: [response.bairro],
          logradouro: [response.logradouro]
        }
    }else{
      return {
        uf: [],
        localidade: [],
        bairro: [],
        logradouro: []
      }
    }
}

export async function getAddressManually(watch:UseFormWatch<InputsInterface>):Promise<CepDataInterface>{
    const data = await fetch(`https://viacep.com.br/ws/${watch().state_for_address}/${
      watch().city}/${watch().neighborhood}/json/`);
    const response = await data.json().catch(error=>alert(error));
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
    console.log(newResponse)
    return newResponse;
}