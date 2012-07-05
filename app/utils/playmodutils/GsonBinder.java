package utils.playmodutils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import play.data.binding.Global;
import play.data.binding.TypeBinder;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

@Global
public class GsonBinder implements TypeBinder<JsonElement> {

  public Object bind(String name, Annotation[] antns, String value, Class klass, Type toType)
      throws Exception {
    return new JsonParser().parse(value);
  }
}