"use client";
import { InformationCircleIcon } from "@heroicons/react/24/outline";
import { useState } from "react";

export default function Page() {
    const [cadastrarSe, setCadastrarSe] = useState<boolean>(false);
    const switchCadastrarSe = () => setCadastrarSe(!cadastrarSe);
    return (
        <main className="bg-neutral-50 text-neutral-950 flex flex-row justify-center items-center w-screen h-screen">
            <div className="h-[90%] w-[60%] relative flex justify-center items-center">
                <div className="w-max absolute top-0 z-10">
                    <h1>Bem-Vindo(a)!</h1>
                    <h2>Insira suas credenciais para acessar o sistema.</h2>
                    <div className="flex flex-row justify-start items-center">
                        <InformationCircleIcon className="aspect-square w-6 mr-2" />
                        <h3>Caso não esteja cadastrado, ultilize o botão <span className="bg-neutral-50 p-2 drop-shadow-md rounded-md ml-2">Cadastrar-se</span></h3>
                    </div>
                </div>
                <form className="flex flex-col items-center mb-4">
                    {cadastrarSe ? (
                        <div className="bg-neutral-50 drop-shadow-md rounded-md p-2">
                            <input type="text" placeholder="Insira seu nome" className="bg-neutral-50" />
                            <input type="email" placeholder="Insira seu email" className="bg-neutral-50" />
                            <input type="text" placeholder="Insira sua nacionalidade" className="bg-neutral-50" />
                            <input type="text" placeholder="Insira seu genero" className="bg-neutral-50" />
                            <div className="flex flex-row justify-between items-center">
                                <input type="text" placeholder="Insira sua senha" className="bg-neutral-50" />
                                <input type="text" placeholder="Re-insira sua senha" className="bg-neutral-50" />
                            </div>
                        </div>
                    ) : (
                        <div className="bg-neutral-50 drop-shadow-md rounded-md p-2">
                            <input type="email" placeholder="Insira seu email" className="bg-neutral-50" />
                            <input type="password" placeholder="Insira sua senha" className="bg-neutral-50" />
                        </div>
                    )}
                    <div className="w-96 flex flex-row justify-between items-center mt-4">
                        <button type="submit">Entrar</button>
                        <button type="button" onClick={switchCadastrarSe}>{cadastrarSe ? 'Voltar' : 'Cadastrar-se'}</button>
                    </div>
                </form>
            </div>
        </main>
    );
}
