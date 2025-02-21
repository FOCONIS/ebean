package io.ebean.querybean.generator;

interface Constants {

  String AT_GENERATED = "@io.ebean.typequery.Generated(\"io.ebean.querybean.kotlin-generator\")";
  String AT_TYPEQUERYBEAN = "@io.ebean.typequery.TypeQueryBean(\"v1\")";

  String MAPPED_SUPERCLASS = "jakarta.persistence.MappedSuperclass";
  String DISCRIMINATOR_VALUE = "jakarta.persistence.DiscriminatorValue";
  String INHERITANCE = "jakarta.persistence.Inheritance";
  String ENTITY = "jakarta.persistence.Entity";
  String EMBEDDABLE = "jakarta.persistence.Embeddable";
  String CONVERTER = "jakarta.persistence.Converter";
  String ONE_TO_MANY = "jakarta.persistence.OneToMany";
  String MANY_TO_MANY = "jakarta.persistence.ManyToMany";
  String EBEAN_COMPONENT = "io.ebean.annotation.EbeanComponent";

  String DBARRAY = "io.ebean.annotation.DbArray";
  String DBJSON = "io.ebean.annotation.DbJson";
  String DBJSONB = "io.ebean.annotation.DbJsonB";
  String DBNAME = "io.ebean.annotation.DbName";

  String MODULEINFO = "io.ebean.config.ModuleInfo";
  String METAINF_MANIFEST = "META-INF/ebean-generated-info.mf";
  String METAINF_SERVICES_MODULELOADER = "META-INF/services/io.ebean.config.EntityClassRegister";

  String AVAJE_LANG_NULLABLE = "io.avaje.lang.Nullable";
  String JAVA_COLLECTION = "java.util.Collection";
}
