import { UseFormWatch } from "react-hook-form";
import InputsInterface from "../(public)/signup/components/form/Interface/InputsInterface";
import CepDataInterface from "../(public)/signup/components/form/Interface/CepData";

export async function tryGetAddressByCep(watch:UseFormWatch<InputsInterface>):Promise<CepDataInterface>{
    if(watch().cep?.length === 8){
        const data = await fetch(`https://viacep.com.br/ws/${watch().cep}/json/`);
        const response = await data.json().catch(error=>console.log(error));
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
    const response = await data.json().catch(error=>console.log(error));
    //SEPARAR OS OBJETOS uf,localidade,bairro e logradouro em arrays separados usando map
    return response;
}