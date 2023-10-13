"use client";
import React, { ReactNode, useState } from "react";
import InputsInterface from "./Interface/InputsInterface";
import issuingBody from "../../../../../../api/issuingBody/issuingBody";
import addressType from "../../../../../../api/addressType/addressType";
import { useForm, SubmitHandler } from "react-hook-form";
import {
  PowerOfAttorney,
  Ocuppation,
  Nationality,
  MaritalStatus,
  UF,
  State,
} from "./InputSelect/InputSelect";
import { FullName, Email, RG } from "./InputText/InputText";
import { CommonLawMarriage } from "./InputRadio/InputRadio";

interface AccordionItemProps {
  title: string;
  children: ReactNode;
  isOpen?: boolean; // Adicione a propriedade isOpen para controlar o estado inicial do accordion
}

const AccordionItem: React.FC<AccordionItemProps> = ({ title, children, isOpen = false }) => {
  const [isAccordionOpen, setIsAccordionOpen] = useState(isOpen);

  return (
    <div>
      <button onClick={() => setIsAccordionOpen(!isAccordionOpen)}>{title}</button>
      {isAccordionOpen && <div>{children}</div>}
    </div>
  );
};

export default function UserForm() {
  const { register, handleSubmit, watch } = useForm<InputsInterface>({
    defaultValues: {
      cepNotFound: false,
    },
  });

  const onSubmit: SubmitHandler<InputsInterface> = (data) => console.log(data);

  return (
    <form style={{ width: "700px", margin: "0 auto" }} onSubmit={handleSubmit(onSubmit)}>
      <div>
        <AccordionItem title="Campos gerais" isOpen={true}>
          <PowerOfAttorney marginBottom={10} register={register} watch={watch} />
          <FullName marginBottom={10} register={register} />
          <Ocuppation marginBottom={10} register={register} watch={watch} />
          <Nationality marginBottom={10} register={register} watch={watch} />
          <Email register={register} />
        </AccordionItem>
      </div>
      <div>
        <AccordionItem title="Campos de documentos">
          <MaritalStatus marginBottom={10} register={register} watch={watch} />
          <CommonLawMarriage marginBottom={10} register={register} />
          <RG marginBottom={10} register={register} />
          <UF marginBottom={10} register={register} watch={watch} />
          <div style={{ marginBottom: 10 }}>
            <label htmlFor="input-issuing-body">Órgão Emissor: </label>
            <select required defaultValue="default" id="input-issuing-body" {...register("issuing_body_id")}>
              <option disabled value="default">-- Escolha um Órgão Emissor --</option>
              {issuingBody.map((issuing) => {
                return (
                  <option key={issuing.abbreviation + issuing.name} value={issuing.id}>
                    {issuing.abbreviation} - {issuing.name}
                  </option>
                );
              })}
            </select>
          </div>
          <div style={{ marginBottom: 10 }}>
            <label htmlFor="input-cpf">CPF: </label>
            <input required type="text" id="input-cpf" {...register("cpf")} />
          </div>
          <div>
            <label htmlFor="input-mother-name">Nome da mãe: </label>
            <input required type="text" id="input-mother-name" {...register("mother_name")} />
          </div>
        </AccordionItem>
      </div>
      {watch().power_of_attorney === "previdenciario" && (
        <div>
          <AccordionItem title="Campos extras">
            <div style={{ marginBottom: 10 }}>
              <label htmlFor="input-cep">CEP: </label>
              <input required type="text" id="input-cep" {...register("cep")} />
            </div>
            <div style={{ marginBottom: 10 }}>
              <label htmlFor="input-cep-not-found">Ative caso o CEP não seja encontrado: </label>
              <input type="checkbox" id="input-cep-not-found" {...register("cepNotFound")} />
            </div>
            <State marginBottom={10} register={register} watch={watch} />
            <div style={{ marginBottom: 10 }}>
              <label htmlFor="input-address-type">Tipo de logradouro: </label>
              <select
                disabled={!watch().cepNotFound}
                defaultValue="default"
                required
                id="input-address-type"
                {...register("address_type_id")}
              >
                <option disabled value="default">-- Escolha o tipo de logradouro --</option>
                {addressType.map((address) => {
                  return (
                    <option key={address.id} value={address.id}>
                      {address.type}
                    </option>
                  );
                })}
              </select>
              <label htmlFor="input-address-name"> Nome: </label>
              <input
                required
                disabled={!watch().cepNotFound}
                placeholder="Ex: Rua Camaleão Astuto"
                type="text"
                id="input-address-name"
                {...register("address_name")}
              />
            </div>
            <div style={{ marginBottom: 10 }}>
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
                  {...register("address_complement")}
                />
              ) : watch().address_complement_type === "qd-lt" && (
                <input
                  placeholder="Adicione a quadra e lote"
                  required
                  type="text"
                  {...register("address_complement")}
                />
              )}
            </div>
            <div>
              <label htmlFor="input-neighborhood">Bairro: </label>
              <input
                required
                disabled={!watch().cepNotFound}
                type="text"
                id="input-neighborhood"
                {...register("neighborhood")}
              />
            </div>
          </AccordionItem>
        </div>
      )}
      {watch().power_of_attorney === "trabalhista" && <h1>Trabalhista</h1>}
      {watch().power_of_attorney === "administrativo" && <h1>Administrativo</h1>}
      {watch().power_of_attorney === "civel" && <h1>Cível</h1>}
      <br />
      <button>Enviar</button>
    </form>
  );
}
