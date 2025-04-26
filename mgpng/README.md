
## Plantilla Base

Para el desarrollo del módulo, se utilizó la plantilla **AVALON** de PrimeNG:

- Página oficial de la plantilla: [Avalon](https://avalon.primeng.org/)
- Código de la plantilla: [Avalon en GitLab](https://gitlab.espe.edu.ec/MarcoDeTrabajo/plantillas-sistemas-utic/demo-template-angular-v17)

Como framework de diseño, se utiliza **PrimeNG v17**:

- Lista de componentes de PrimeNG: [PrimeNG use](https://primeng.org/autocomplete)

## Inicar ambiente de desarrollo
comando para instalar dependencias del proyecto
```bash
  npm install
```
comando para iniciar el proyecto
```bash
  ng serve 
```


El proyecto se desplegará en la url 
http://localhost:4200

## Instrucciones para el desarrollo
Dentro de la carpeta src/componentes, encontrará la carpeta de cada módulo

## Estructura 

Proyecto/  
├── src/  
│   ├── app/  
│   │   ├── componentes/  
│   │   │   ├── modulo1/  
│   │   │   │   ├── componente1 --> (parte funcional específica del módulo )  /  
│   │   │   │   ├── componente2  --> (parte funcional específica del módulo ) /  
│   │   │   │   ├── interfaces   --> ( interfaces TypeScript)/   
│   │   │   │   ├── xxxxx-routing-module.ts --> (enrutamiento para definir rutas de navegación) /             
│   │   │   │   ├── xxxxx.module.ts --> (declara, importa y exporta componentes y servicios)/  
│   │   │   │   ├── xxxxx.service.ts  -->(interacciones con la API)/  
│   │   │   │   └── submenu.ts/  --> (define opciones de submenús)  
│   │   │   └── modulo2/     
│   │   │     
├── .gitignore  
├── README.md   
