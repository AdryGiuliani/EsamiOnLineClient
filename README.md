# EsamiOnline – Frontend Module

This repository contains the **frontend module** of the distributed Software Engineering project **EsamiOnline**.

The frontend:

- runs on **JBoss/WildFly 32**
- is built with **Jakarta Faces (JSF)**, **PrimeFaces**, **PrimeFlex**, and **FontAwesome**
- interacts with the **EsamiOnline gRPC server** from the backend project

---

## Tech Stack

- Application server: **WildFly 32**
- UI framework: **Jakarta Faces (JSF)**
- Components: **PrimeFaces**
- Layout / utilities: **PrimeFlex**
- Icons: **FontAwesome**
- Languages: **HTML**, **Java** (backing beans and configuration)

---

## Backend Dependency

The backend server is included as a Maven dependency and is provided by:

- **EsamiOnLine** (server, gRPC): <https://github.com/AdryGiuliani/EsamiOnLine>

To run the full system, the backend project must be built and available in your local Maven repository or otherwise resolved.

---

## High‑Level Setup

The typical setup used for this module is:

- Configure **WildFly 32** as the application server.
- Deploy this JSF application to WildFly.
- Start:
  - the gRPC server main class from the **EsamiOnLine** backend, and
  - WildFly with this frontend deployed.
- Use the JSF pages:
  - `/login.xhtml` for client login
  - `/admin.xhtml` for the admin interface

See the project’s configuration files and `pom.xml` for exact deployment details.

---

## Project Structure

```text
EsamiOnLineClient/
  src/
    main/
      java/        # JSF backing beans, configuration, gRPC client use
      webapp/
        login.xhtml
        admin.xhtml
        WEB-INF/
        ...        # other JSF views and resources
  pom.xml
```

---

## Original Italian Description

> Progetto Ing‑sw distribuito Esami‑online (modulo frontend).  
> Il frontend è realizzato con JBoss/WildFly 32, Jakarta Faces, PrimeFaces, PrimeFlex e FontAwesome.  
> Il backend gRPC è disponibile come dipendenza Maven nel `pom` e nel repository:  
> <https://github.com/AdryGiuliani/EsamiOnLine>.  
> Le pagine principali sono `/login.xhtml` (client) e `/admin.xhtml` (amministrazione).

---

## License
