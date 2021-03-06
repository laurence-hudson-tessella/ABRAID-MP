<#--
    The page template, including header and footer.
    Copyright (c) 2014 University of Oxford
-->
<#macro page title endOfHead="" bootstrapData="" templates="" mainjs="/js/shared/kickstart/default" includeNavBar=true includeFooter=true>
<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html class="no-js">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">

        <link rel="shortcut icon" href="<@spring.url '/favicon.ico'/>">

        <title>${title?html}</title>

        <meta name="description" content="">
        <meta name="viewport" content="width=device-width">

        <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.1/css/bootstrap.min.css">
        <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.1/css/bootstrap-theme.min.css">
        <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.2.0/css/font-awesome.min.css">
        <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/jasny-bootstrap/3.1.2/css/jasny-bootstrap.min.css">
        <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.3.0/css/datepicker3.min.css">

        ${endOfHead}

        <link rel="stylesheet" href="<@spring.url '/css/shared/main.css'/>">
    </head>
    <body>
        <#if includeNavBar>
            <#include "../../layout/navbar.ftl"/>
        </#if>
        <div id="common">
            <#nested/>
        </div>
        <#if includeFooter>
            <#include "footer.ftl"/>
        </#if>

        <!-- Base url -->
        <script>
            var baseUrl = "<@spring.url '/'/>";
        </script>

        <!-- Bootstrapped JS data for KO view models -->
        ${bootstrapData}

        <!-- Templates -->
        ${templates}
        <script type="text/html" id="validation-template">
            <!-- ko if: field.rules().length != 0 -->
                <span class="input-group-addon" data-container="body" data-bind="css: field.isValid() ? 'bg-success-important' : 'bg-danger-important', tooltip: { title: field.error, placement: 'right' } ">
                    <i class="fa fa-lg" data-bind="css: field.isValid() ? 'text-success fa-check-circle' : 'text-danger fa-exclamation-circle'"></i>
                </span>
            <!-- /ko -->
        </script>

        <!-- Require -->
        <script type="text/javascript" data-main="<@spring.url '${mainjs}' />" src="//cdnjs.cloudflare.com/ajax/libs/require.js/2.1.11/require.min.js"></script>
    </body>
</html>
</#macro>
